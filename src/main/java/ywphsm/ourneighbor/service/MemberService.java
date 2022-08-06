package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.EmailToken;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.email.TokenService;
import javax.persistence.EntityManager;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
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
    private final EntityManager em;

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
        String birthYear = birthDate.substring(0, 3);

        return nowYear - Integer.parseInt(birthYear);
    }

    //비밀번호 인코딩
    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    //이메일 토큰만료, 인증된 이메일로 변경
    @Transactional
    public boolean confirmEmail(String tokenId) {
        EmailToken findToken = tokenService.findByIdAndExpirationDateAfterAndExpired(tokenId);
        if (findToken != null) {
            findToken.useToken();   // 토큰 만료 로직을 구현해주면 된다. ex) expired 값을 true로 변경
            Member findMember = findOne(findToken.getMemberId());
            findMember.emailConfirmSuccess(); // 유저의 이메일 인증 값 변경 로직을 구현해주면 된다.
            return true;
        }

        return false;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    //회원수정 변경 감지
    @Transactional
    public void update(Long id, String nickname, String phoneNumber) {
        Member member = memberRepository.findById(id).get();

        member.update(nickname, phoneNumber);
        em.flush();
        em.clear();
    }

    //비밀번호 확인
    public boolean passwordCheck(String password, String beforePassword) {
        return passwordEncoder.matches(beforePassword, password);
    }

    //비밀번호 수정 변경 감지
    public void updatePassword(Long id, String encodedPassword) {
        Member member = memberRepository.findById(id).get();

        member.updatePassword(encodedPassword);
        em.flush();
        em.clear();
    }

}
