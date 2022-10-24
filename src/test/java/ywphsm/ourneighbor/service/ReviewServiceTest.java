package ywphsm.ourneighbor.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.OurNeighborApplication;
import ywphsm.ourneighbor.domain.*;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ywphsm.ourneighbor.domain.QReview.*;
import static ywphsm.ourneighbor.domain.member.QMember.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
@Transactional
class ReviewServiceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    ReviewService reviewService;

    @Autowired
    StoreService storeService;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "USER", roles = "USER")
    @DisplayName("리뷰 등록")
    void saveReview() throws Exception {
        Long storeId = 93L;
        Long memberId = 584L;
        String content = "존맛탱";
        Integer rating = 5;

//        new MockMultipartFile("필드명", storedFileName, contentType, 서버에 있는 파일 경로)
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
//                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
                new FileInputStream("C:/Users/HOME/Desktop/JAVA/menu_file/5cf53790-54a5-4c5f-9709-0394d58cec94.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        mvc.perform(multipart("/user/review")
                        .file(file)
                        .param("content", content)
                        .param("rating", String.valueOf(rating))
                        .param("storeId", String.valueOf(storeId))
                        .param("memberId", String.valueOf(memberId)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Review> reviewList = storeService.findById(storeId).getReviewList();

        for (Review review : reviewList) {
            if (review.getMember().getId().equals(memberId) && review.getStore().getId().equals(storeId)) {
                assertThat(review.getContent()).isEqualTo("존맛탱");
                assertThat(review.getRating()).isEqualTo(5);
                assertThat(review.getFile().getUploadedFileName()).isEqualTo("test.png");
            }
        }
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @DisplayName("리뷰 삭제")
    void deleteReview() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png",
                new FileInputStream("C:/Users/HOME/Desktop/JAVA/menu_file/5cf53790-54a5-4c5f-9709-0394d58cec94.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        Long storeId = 93L;
        Long memberId = 584L;

        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .storeId(storeId)
                .memberId(memberId)
                .content("존맛탱")
                .rating(5)
                .file(file)
                .build();

        Long reviewId = reviewService.save(dto);

        mvc.perform(delete("/review/delete/" + storeId)
                                .param("storeId", String.valueOf(storeId))
                                .param("reviewId", String.valueOf(reviewId)))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> reviewService.findOne(reviewId))
                .isInstanceOf(NoSuchElementException.class);
    }
}