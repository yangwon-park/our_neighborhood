package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.email.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 회원 가입
    @Transactional
    public Long save(MemberDTO.Add dto) {

        int age = ChangeBirthToAge(dto.getBirthDate());
        //패스워드 암호화
        String encodedPassword = encodedPassword(dto.getPassword());

        Member member = new Member(dto.getUsername(), dto.getBirthDate(),
                age, dto.getPhoneNumber(),
                dto.getGender(), dto.getUserId(), encodedPassword,
                dto.getEmail(), dto.getNickname(), Role.USER);

        memberRepository.save(member);
        return member.getId();
    }

    // 회원 한명 조회
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + memberId));
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

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + userId));
    }

    //회원수정시 닉네임 변경
    @Transactional
    public void updateMember (Long id, String nickname, String email) {
        Member member = findById(id);

        member.updateMember(nickname, email);
    }

    //회원수정시 전화번호 변경
    @Transactional
    public void updatePhoneNumber(Long id, String phoneNumber) {
        Member member = findById(id);
        member.updatePhoneNumber(phoneNumber);

    }

    //비밀번호 확인
    public boolean passwordCheck(String password, String beforePassword) {
        return passwordEncoder.matches(beforePassword, password);
    }

    //비밀번호 수정 변경 감지(회원수정)
    @Transactional
    public void updatePassword(Long id, String encodedPassword) {
        Member member = findById(id);
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
        params.put("text", "Our neighborhood 휴대폰인증 메시지 : 인증번호는" + "[" + cerNum + "]" + "입니다.");

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
    public String sendEmailByUserId(String email) {

        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return "없는 이메일 입니다.";
        }
        if (member.getUserId() == null) {
            return "해당 계정은 아이디가 존재하지 않습니다";
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);   //보낼 이메일주소 추가
        mailMessage.setSubject("아이디 찾기 Our neighborhood");  //제목
        mailMessage.setText("회원님의 아이디는 " + member.getUserId() + "입니다.");
        emailService.sendEmail(mailMessage);
        log.info("아이디 찾기 이메일 발송 완료 uesrId={}", member.getUserId());

        return "성공";
    }

    //비밀번호 찾기
    @Transactional
    public String sendEmailByPassword(String email, String userId) {

        Random rand = new Random();
        String temporaryPassword = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            temporaryPassword += ran;
        }

        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return "없는 이메일 입니다.";
        }
        if (!member.getUserId().equals(userId)) {
            return "해당 계정의 아이디와 일치하지 않습니다";
        }

        //임시 비밀번호로 변경
        member.updatePassword(encodedPassword(temporaryPassword));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);   //보낼 이메일주소 추가
        mailMessage.setSubject("비밀번호 찾기 Our neighborhood");  //제목
        mailMessage.setText("임시 비밀번호를 발급했습니다.");
        mailMessage.setText("임시 비밀번호는 " + temporaryPassword + "입니다.");
        mailMessage.setText("로그인후 마이페이지에서 비밀번호를 변경해 주세요.");
        emailService.sendEmail(mailMessage);
        log.info("비밀번호 찾기 이메일 발송 완료 임시비밀번호={}", temporaryPassword);

        return "성공";
    }

    //회원탈퇴
    @Transactional
    public void withdrawal(Long id) {
        memberRepository.findById(id).ifPresent(memberRepository::delete);
    }

    public Long updateRole(Long memberId, String role) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + memberId));


        Role findRole = Role.of(role);

        findMember.updateRole(findRole);

        return memberId;
    }

    //찜 상태 확인 - detail에 뿌리기 위함
    public boolean likeStatus(Long memberId, Long storeId) {
        Member member = findById(memberId);

        List<MemberOfStore> memberOfStoreList = member.getMemberOfStoreList();
        if (!memberOfStoreList.isEmpty()) {
            for (MemberOfStore memberOfStore : memberOfStoreList) {
                if (memberOfStore.getStore().getId().equals(storeId) && memberOfStore.isStoreLike()) {
                    return true;
                }
            }
        }
        return false;
    }

}
