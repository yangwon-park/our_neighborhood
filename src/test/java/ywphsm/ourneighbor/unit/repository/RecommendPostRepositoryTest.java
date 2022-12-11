package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.repository.recommendpost.RecommendPostRepository;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static ywphsm.ourneighbor.api.dto.RecommendKind.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecommendPostRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    RecommendPostRepository recommendPostRepository;

    @BeforeEach
    void beforeEach() {
        RecommendPost post1 = RecommendPost.builder()
                .recommendKind(NORMAL)
                .header("평범1")
                .content("평범1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post2 = RecommendPost.builder()
                .recommendKind(COLD)
                .header("추움1")
                .content("추움1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post3 = RecommendPost.builder()
                .recommendKind(RAINY)
                .header("비1")
                .content("비1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post4 = RecommendPost.builder()
                .recommendKind(DOWNPOUR)
                .header("폭우1")
                .content("폭우1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post5 = RecommendPost.builder()
                .recommendKind(SNOWY)
                .header("눈1")
                .content("눈1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post6 = RecommendPost.builder()
                .recommendKind(SUNNYANDGOODPM)
                .header("최고의 날씨1")
                .content("최고의 날씨1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post7 = RecommendPost.builder()
                .recommendKind(BADPM)
                .header("미세먼지 매우 심함1")
                .content("미세먼지 매우 심함1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post8 = RecommendPost.builder()
                .recommendKind(VERYCLOUDY)
                .header("매우 흐림1")
                .content("매우 흐림1")
                .hashtagList(new ArrayList<>())
                .build();

        RecommendPost post9 = RecommendPost.builder()
                .recommendKind(NORMAL)
                .header("평범9")
                .content("평범9")
                .hashtagList(new ArrayList<>())
                .build();

        tem.persist(post1);
        tem.persist(post2);
        tem.persist(post3);
        tem.persist(post4);
        tem.persist(post5);
        tem.persist(post6);
        tem.persist(post7);
        tem.persist(post8);
        tem.persist(post9);
    }

    @Test
    @DisplayName("맑고 미세먼지 없는 날")
    void should_FindRecommendPost_When_ByRecommendKindIsSUNNYANDGOODPM() {
        String skyStatus = "SUNNY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.0";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(SUNNYANDGOODPM);
    }

    @Test
    @DisplayName("미세먼지 심한 날")
    void should_FindRecommendPost_When_ByRecommendKindIsBADPM() {
        String skyStatus = "SUNNY";
        String pm10Value = "81";
        String tmp = "15";
        String pcp = "3.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(BADPM);
    }

    @Test
    @DisplayName("비오는 날")
    void should_FindRecommendPost_When_ByRecommendKindIsRAINY() {
        String skyStatus = "RAINY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "3.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(RAINY);
    }


    @Test
    @DisplayName("폭우오는 날")
    void should_FindRecommendPost_When_ByRecommendKindIsDOWNPOUR() {
        String skyStatus = "RAINY";
        String pm10Value = "30";
        String tmp = "25";
        String pcp = "7.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(DOWNPOUR);
    }

    @Test
    @DisplayName("눈오는 날")
    void should_FindRecommendPost_When_ByRecommendKindIsSNOWY() {
        String skyStatus = "SNOWY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(SNOWY);
    }

    @Test
    @DisplayName("매우 흐린 날")
    void should_FindRecommendPost_When_ByRecommendKindIsVERYCLOUDY() {
        String skyStatus = "VERYCLOUDY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.1";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(VERYCLOUDY);
    }

    @Test
    @DisplayName("추운 날")
    void should_FindRecommendPost_When_ByRecommendKindIsCLOUDY() {
        String skyStatus = "CLOUDY";
        String pm10Value = "30";
        String tmp = "5";
        String pcp = "0.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(COLD);
    }

    @Test
    @DisplayName("평범한 날")
    void should_FindRecommendPost_When_ByRecommendKindIsNORMAL() {
        String skyStatus = "CLOUDY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.6";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        PageRequest pageRequest = PageRequest.of(0, 5);

        RecommendPost recommendPost = recommendPostRepository.findByRecommendKind(cond, pageRequest).get(0);

        assertThat(recommendPost.getRecommendKind()).isEqualTo(NORMAL);
    }

    @Test
    @DisplayName("날씨 상태별 추천 게시글의 개수 조회")
    void should_Count_When_ByRecommendKind() {
        /*
            NORMAL만 2개
         */
        Long cnt = recommendPostRepository.countByRecommendKind(NORMAL);

        assertThat(cnt).isEqualTo(2);
    }


    private static RecommendKind getRecommendKind(String skyStatus, String pm10Value, String tmp, String pcp) {
        double rainCriteria = 7.6;
        int pm10ValueBadCriteria = 80;
        int pm10ValueGoodCriteria = 30;
        int coldCriteria = 5;

        if (Integer.parseInt(pm10Value) > pm10ValueBadCriteria) {
            return BADPM;
        }

        if (skyStatus.equals("RAINY") || skyStatus.equals("VERYCLOUDY") || skyStatus.equals("SNOWY")) {
            if (Double.parseDouble(pcp) >= rainCriteria) {
                return DOWNPOUR;
            }
            return of(skyStatus);
        }

        if (Integer.parseInt(tmp) <= coldCriteria) {
            return COLD;
        }

        if (skyStatus.equals("SUNNY") && Integer.parseInt(pm10Value) <= pm10ValueGoodCriteria) {
            return SUNNYANDGOODPM;
        }

        // 별 특색없는 하루
        if (skyStatus.equals("CLOUDY") || skyStatus.equals("SUNNY")) {
            return NORMAL;
        }

        return NORMAL;
    }
}
