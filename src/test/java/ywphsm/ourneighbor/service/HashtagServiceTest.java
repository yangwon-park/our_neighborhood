package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class HashtagServiceTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @LocalServerPort
    private int port;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    MockHttpSession session;

    @Autowired
    HashtagService hashtagService;

    @Autowired
    HashtagOfStoreService hashtagOfStoreService;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

    }

    @Test
    @DisplayName("해쉬태그 저장 후 삭제")
    void saveAndDelete() {
        String name = "hashtag";

        HashtagDTO dto = HashtagDTO.builder()
                .name(name)
                .build();

        Hashtag hashtag = hashtagService.save(dto);

        assertThat(hashtag.getName()).isEqualTo(name);

        hashtagService.delete(hashtag.getId());

        assertThatThrownBy(() -> hashtagService.findById(hashtag.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("매장과 연관된 해쉬태그 저장 후 삭제")
    void deleteHashtag() {
        String name = "hashtag";

        HashtagDTO dto = HashtagDTO.builder()
                .name(name)
                .build();

        Hashtag hashtag = hashtagService.save(dto);

        assertThat(hashtag.getName()).isEqualTo(name);

        String url = "http://localhost:" + port + "/seller/hashtag/" + hashtag.getId();

//        mvc.perform(delete(url).param())

    }

    @Test
    @DisplayName("매장 상위9개_해쉬태그_조회")
    void findTop9HashtagInAStore() {
        Long storeId = 28L;

        List<HashtagOfStoreDTO.WithCount> list =
                hashtagOfStoreService.findHashtagAndCountByStoreIdOrderByCountDescTop9(storeId);

        for (HashtagOfStoreDTO.WithCount dto : list) {
            System.out.println("dto = " + dto);
        }
    }
}