package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.EmailToken;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.email.EmailService;
import ywphsm.ourneighbor.service.email.TokenService;
import javax.persistence.EntityManager;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 한명 조회
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 닉네임 중복 체크
    public Member doubleCheck(String nickname) {
        return memberRepository.findByNickname(nickname).orElse(null);
    }

    //생년월일 나이 계산
    public int ChangeBirthToAge(String birthDate) {

        int nowYear = LocalDateTime.now().getYear();
        String birthYear = birthDate.substring(0, 4);

        return nowYear - Integer.parseInt(birthYear) + 1;
    }

    //비밀번호 인코딩
    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    //이메일 토큰만료, 인증된 이메일로 변경
    @Transactional
    public boolean confirmEmail(String tokenId) {
        EmailToken findToken = tokenService.findByIdAndExpirationDateAfterAndExpired(tokenId);  //유효한 토큰 찾기
        if (findToken != null) {
            findToken.useToken();   // 토큰 만료 로직을 구현 ex) expired 값을 true로 변경
            Member findMember = findOne(findToken.getMemberId());
            findMember.emailConfirmSuccess(); // 유저의 이메일 인증 값 변경 로직을 구현해주면 된다.
            return true;
        }

        return false;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    //회원수정시 닉네임 변경
    @Transactional
    public void updateNickname(Long id, String nickname) {
        Member member = memberRepository.findById(id).get();

        member.updateNickname(nickname);
    }

    //회원수정시 전화번호 변경
    @Transactional
    public void updatePhoneNumber(Long id, String phoneNumber) {
        Member member = memberRepository.findById(id).get();

        member.updatePhoneNumber(phoneNumber);
    }

    //비밀번호 확인
    public boolean passwordCheck(String password, String beforePassword) {
        return passwordEncoder.matches(beforePassword, password);
    }

    //비밀번호 수정 변경 감지(회원수정)
    @Transactional
    public void updatePassword(Long id, String encodedPassword) {
        Member member = memberRepository.findById(id).get();

        member.updatePassword(encodedPassword);
    }

    //비밀번호 찾기 수정 변경 감지(비밀번호 찾기)
    @Transactional
    public void updatePassword(String userId, String encodedPassword) {
        Member member = memberRepository.findByUserId(userId).get();

        member.updatePassword(encodedPassword);
    }

    //비밀번호 찾기시 있는 아이디인지 확인
    public Member userIdCheck(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    //휴대폰에 인증번호 발송
    public void certifiedPhoneNumber(String phoneNumber, String cerNum) {
        String api_key = "NCS0EUZ1M5AZKUFA";
        String api_secret = "YOHXWEBYGAWYTGPCTUXKXJOEZOF7VYKZ";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);    // 수신전화번호
        params.put("from", "010383523755");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "Our neighborhood 휴대폰인증 메시지 : 인증번호는" + "["+cerNum+"]" + "입니다.");

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

    }

    //휴대폰번호 중복검사
    public Member findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    //아이디 찾기
    public String findUserId(String receiverEmail) {

        String userId = memberRepository.findByEmail(receiverEmail).orElse(null).getUserId();

        if (userId != null) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(receiverEmail);   //보낼 이메일주소 추가
            mailMessage.setSubject("아이디 찾기 Our neighborhood");  //제목
            mailMessage.setText("회원님의 아이디는 " + userId + "입니다.");
            emailService.sendEmail(mailMessage);
        }

        return userId;
    }

    //회원탈퇴
    @Transactional
    public void withdrawal(Long id) {
        memberRepository.findById(id).ifPresent(memberRepository::delete);
    }
}
