package ywphsm.ourneighbor.service.login;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member login(String userId, String password) {

        return memberRepository.findByUserId(userId)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElse(null);

    }



}
