package ywphsm.ourneighbor.repository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.repository.member.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    static Member saveMember() {
        return new Member("username", "991002", 20, "01011111111",
                0, "beforeId", "password",
                "testEmail@email.com", "testNickname", Role.USER);
    }

    @Test
    void findByNickname() {
        Member member = saveMember();
        memberRepository.save(member);
        Member findMember = memberRepository.findByNickname(member.getNickname()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findByUserId() {
        Member member = saveMember();
        memberRepository.save(member);
        Member findMember = memberRepository.findByUserId(member.getUserId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findByEmail() {
        Member member = saveMember();
        memberRepository.save(member);
        Member findMember = memberRepository.findByEmail(member.getEmail()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findByPhoneNumber() {
        Member member = saveMember();
        memberRepository.save(member);
        Member findMember = memberRepository.findByPhoneNumber(member.getPhoneNumber()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}