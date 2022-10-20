package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.repository.hashtagofstore.dto.HashtagOfStoreCountDTO;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class HashtagServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    HashtagOfStoreService hashtagOfStoreService;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Member member1 = new Member("kkk", "kkk", "user1",
                "유저1", null, "010-1234-1234", 19, 0, Role.USER);

        Member member2 = new Member("JJJ", "JJJ", "user2",
                "유저2", null, "010-1234-1234", 35, 1, Role.USER);

        Member member3 = new Member("ㅁㅁㅁ", "ㅁㅁㅁ", "user3",
                "유저3", null, "010-1234-1234", 25, 0, Role.USER);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
    }

    @Test
    void 해쉬태그_조회() {
        List<HashtagOfStoreCountDTO> list =
                hashtagOfStoreService.findHashtagCountGroupByStoreTop9();

        for (HashtagOfStoreCountDTO dto : list) {
            System.out.println("dto = " + dto);
        }
    }
}