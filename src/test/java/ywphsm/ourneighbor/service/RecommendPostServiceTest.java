package ywphsm.ourneighbor.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class RecommendPostServiceTest {

    @Autowired
    RecommendPostService recommendPostService;

    @Test
    void 맑고_미세먼지_없는_날() {
        String skyStatus = "SUNNY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.0";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.SUNNYANDGOODPM);
    }

    @Test
    void 비오는날() {
        String skyStatus = "RAINY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "3.6";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.RAINY);
    }

    @Test
    void 폭우() {
        String skyStatus = "RAINY";
        String pm10Value = "30";
        String tmp = "25";
        String pcp = "7.6";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.DOWNPOUR);
    }

    @Test
    void 눈오는_날() {
        String skyStatus = "SNOWY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.6";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        System.out.println("result = " + result);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.SNOWY);
    }

    @Test
    void 매우_흐린날_() {
        String skyStatus = "VERYCLOUDY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.1";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.VERYCLOUDY);
    }
    @Test
    void 추운_날() {
        String skyStatus = "CLOUDY";
        String pm10Value = "30";
        String tmp = "5";
        String pcp = "0.6";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.COLD);
    }

    @Test
    void 평범한_날1() {
        String skyStatus = "CLOUDY";
        String pm10Value = "30";
        String tmp = "15";
        String pcp = "0.6";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.NORMAL);
    }


    @Test
    void 평범한_날2() {
        String skyStatus = "SUNNY";
        String pm10Value = "45";
        String tmp = "15";
        String pcp = "0.0";
        RecommendPostDTO.Simple result =
                recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        assertThat(result.getRecommendKind()).isEqualTo(RecommendKind.NORMAL);
    }
}