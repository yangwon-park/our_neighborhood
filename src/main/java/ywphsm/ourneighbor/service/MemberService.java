package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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






}
