package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static ywphsm.ourneighbor.domain.member.QMember.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Member member1 = new Member("kkk", "kkk", "user1",
                "유저1", null, "010-1234-1234", 19, 0);

        Member member2 = new Member("JJJ", "JJJ", "user2",
                "유저2", null, "010-1234-1234", 35, 1);

        Member member3 = new Member("ㅁㅁㅁ", "ㅁㅁㅁ", "user3",
                "유저3", null, "010-1234-1234", 25, 0);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
    }

    @Test
    @DisplayName("회원 가입")
    void join() {
        Member member4 = new Member("kkk", "kkk", "user4",
                "유저4", null, "010-1234-1234", 19, 0);

        Long memberId = memberService.join(member4);

        Member findMember = memberService.findOne(memberId);

        assertThat(memberId).isEqualTo(findMember.getId());
    }

    @Test
    @DisplayName("10대, 20대인 회원 찾기")
    void findMembers1020() {
        List<Member> memberList = queryFactory
                .select(member)
                .from(member)
                .where(member.age.goe(10).and(member.age.lt(30)))
                .fetch();

        assertThat(memberList).extracting("username").contains("user1", "user3");
    }
}
















