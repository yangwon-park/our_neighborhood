package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.EmailToken;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.email.TokenService;


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
        Optional<Member> findMembers = memberRepository.findByNickname(nickname);
        return findMembers.orElse(null);
    }

    //비밀번호 인코딩
    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    //이메일 토큰만료, 인증된 이메일로 변경
    @Transactional
    public void confirmEmail(String tokenId) {
        EmailToken findToken = tokenService.findByIdAndExpirationDateAfterAndExpired(tokenId);
        if (findToken != null) {
            findToken.useToken();   // 토큰 만료 로직을 구현해주면 된다. ex) expired 값을 true로 변경
            Member findMember = findOne(findToken.getMemberId());
            findMember.emailConfirmSuccess(); // 유저의 이메일 인증 값 변경 로직을 구현해주면 된다.
        }

    }
}
