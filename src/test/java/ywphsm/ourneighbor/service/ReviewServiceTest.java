package ywphsm.ourneighbor.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.service.store.StoreService;

import java.io.FileInputStream;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
    @DisplayName("리뷰 등록 후 삭제")
    void saveReview() throws Exception {
        Long storeId = 24L;
        Long memberId = 453L;
        String content = "존맛탱";
        Integer rating = 5;

//        new MockMultipartFile("필드명", storedFileName, contentType, 서버에 있는 파일 경로)
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/785c984e-fab2-4422-9833-646d94b631ae.jpg"));
//                new FileInputStream("C:/Users/HOME/Desktop/JAVA/menu_file/5cf53790-54a5-4c5f-9709-0394d58cec94.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));
        JSONObject json = new JSONObject();

        json.put("value", "해쉬태그1");
        json.put("value", "해쉬태그2");

        JSONArray array = new JSONArray();
        array.add(json);

        ResultActions resultActions = mvc.perform(multipart("/user/review")
                        .file(file)
                        .param("content", content)
                        .param("rating", String.valueOf(rating))
                        .param("storeId", String.valueOf(storeId))
                        .param("memberId", String.valueOf(memberId))
                        .param("hashtag", String.valueOf(array)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Review> reviewList = storeService.findById(storeId).getReviewList();

        for (Review review : reviewList) {
            if (review.getMember().getId().equals(memberId) && review.getStore().getId().equals(storeId)) {
                assertThat(review.getContent()).isEqualTo("존맛탱");
                assertThat(review.getRating()).isEqualTo(5);
//                assertThat(review.getFileList().getUploadedFileName()).isEqualTo("test.png");
            }
        }

        Long reviewId = storeService.findById(storeId).getReviewList().get(0).getId();

        mvc.perform(delete("/review/delete/" + storeId)
                        .param("storeId", String.valueOf(storeId))
                        .param("reviewId", String.valueOf(reviewId)))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> reviewService.findById(reviewId))
                .isInstanceOf(NoSuchElementException.class);
    }
}