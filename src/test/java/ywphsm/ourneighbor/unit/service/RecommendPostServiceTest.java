package ywphsm.ourneighbor.unit.service;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.api.dto.SkyStatus;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagUtil;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.recommendpost.RecommendPostRepository;
import ywphsm.ourneighbor.service.RecommendPostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static ywphsm.ourneighbor.api.dto.RecommendKind.*;
import static ywphsm.ourneighbor.domain.hashtag.HashtagUtil.*;

@ExtendWith(MockitoExtension.class)
public class RecommendPostServiceTest {

    @Mock
    RecommendPostRepository recommendPostRepository;

    @Mock
    HashtagRepository hashtagRepository;

    @InjectMocks
    RecommendPostService recommendPostService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("추천 장소 게시글 저장 - 기존에 존재하는 해쉬태그")
    void should_SaveRecommendPost() throws ParseException {
        // given
        String hashtagJson = "[{\"value\":\"해쉬태그\"}]";

        Hashtag hashtag = Hashtag.builder()
                .name("해쉬태그")
                .build();

        RecommendPostDTO.Add dto = RecommendPostDTO.Add.builder()
                .recommendKind(NORMAL)
                .header("제목")
                .hashtag(hashtagJson)
                .content("컨텐츠")
                .build();

        RecommendPost entity = dto.toEntity();
        Long mockPostId = 1L;

        setField(entity, "id", mockPostId);

        List<String> hashtagNameList = getHashtagNameList(hashtagJson);

        for (String name : hashtagNameList) {
            given(hashtagRepository.existsByName(name)).willReturn(true);
            given(hashtagRepository.findByName(name)).willReturn(Optional.ofNullable(hashtag));
            given(recommendPostRepository.save(any())).willReturn(entity);
        }

        // when
        Long id = recommendPostService.save(dto);

        // then
        for (String name : hashtagNameList) {
            then(hashtagRepository).should().existsByName(name);
            then(hashtagRepository).should().findByName(name);
        }

        assertThat(mockPostId).isEqualTo(id);
    }

    @Test
    @DisplayName("날씨 기반으로 추천 장소 게시글 조회 - 날씨 조건에 맞는 게시글이 있는 경우")
    void should_FineRecommendPost_When_ByWeatherCondExists() {
        // given
        String skyStatus = SkyStatus.SUNNY.getKey();
        String pm10Value = "0";
        String tmp = "18";
        String pcp = "0";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        RecommendPost post1 = RecommendPost.builder()
                .recommendKind(SUNNYANDGOODPM)
                .header("매우 좋은 날씨")
                .content("매우 좋은 날씨")
                .hashtagList(new ArrayList<>())
                .build();

//        RecommendPost post2 = RecommendPost.builder()
//                .recommendKind(SUNNYANDGOODPM)
//                .header("매우 좋은 날씨2")
//                .content("매우 좋은 날씨2")
//                .hashtagList(new ArrayList<>())
//                .build();

        List<RecommendPost> list = new ArrayList<>();
        list.add(post1);
//        list.add(post2);

        int cnt = list.size();
        int idx = (int) (Math.random() * cnt);
        PageRequest pageRequest = PageRequest.of(idx, 1);

        given(recommendPostRepository.countByRecommendKind(cond)).willReturn(Long.valueOf(cnt));
        given(recommendPostRepository.findByRecommendKind(cond, pageRequest)).willReturn(list);

        // when
        RecommendPostDTO.Simple dto = recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        // then
        then(recommendPostRepository).should().countByRecommendKind(cond);
        then(recommendPostRepository).should().findByRecommendKind(cond, pageRequest);
        assertThat(RecommendPostDTO.Simple.of(list.get(0))).isEqualTo(dto);
    }

    @Test
    @DisplayName("날씨 기반으로 추천 장소 게시글 조회 - 날씨 조건에 맞는 게시글이 없는 경우")
    void should_FineRecommendPost_When_ByWeatherCondExistsNot() {
        // given
        String skyStatus = SkyStatus.SUNNY.getKey();
        String pm10Value = "0";
        String tmp = "18";
        String pcp = "0";

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        RecommendPost post = RecommendPost.builder()
                .recommendKind(NORMAL)
                .header("평범한 날씨")
                .content("평범한 날씨")
                .hashtagList(new ArrayList<>())
                .build();

        List<RecommendPost> list = new ArrayList<>();
        list.add(post);

        int cnt = 0;

        PageRequest pageRequest = PageRequest.of(0, 1);

        given(recommendPostRepository.countByRecommendKind(cond)).willReturn(Long.valueOf(cnt));
        given(recommendPostRepository.countByRecommendKind(NORMAL)).willReturn(Long.valueOf(list.size()));
        given(recommendPostRepository.findByRecommendKind(NORMAL, pageRequest)).willReturn(list);

        // when
        RecommendPostDTO.Simple dto = recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp);

        System.out.println(RecommendPostDTO.Simple.of(list.get(0)));
        System.out.println("dto = " + dto);

        // then
        then(recommendPostRepository).should().countByRecommendKind(cond);
        then(recommendPostRepository).should().countByRecommendKind(NORMAL);
        then(recommendPostRepository).should().findByRecommendKind(NORMAL, pageRequest);
        assertThat(RecommendPostDTO.Simple.of(list.get(0))).isEqualTo(dto);
    }


    private static RecommendKind getRecommendKind(String skyStatus, String pm10Value, String tmp, String pcp) {
        double rainCriteria = 7.6;
        int pm10ValueBadCriteria = 80;
        int pm10ValueGoodCriteria = 30;
        int coldCriteria = 5;

        if (Integer.parseInt(pm10Value) > pm10ValueBadCriteria) {
            return RecommendKind.BADPM;
        }

        if (skyStatus.equals("RAINY") || skyStatus.equals("VERYCLOUDY") || skyStatus.equals("SNOWY")) {
            if (Double.parseDouble(pcp) >= rainCriteria) {
                return RecommendKind.DOWNPOUR;
            }
            return RecommendKind.of(skyStatus);
        }

        if (Integer.parseInt(tmp) <= coldCriteria) {
            return RecommendKind.COLD;
        }

        if (skyStatus.equals("SUNNY") && Integer.parseInt(pm10Value) <= pm10ValueGoodCriteria) {
            return SUNNYANDGOODPM;
        }

        // 별 특색없는 하루
        if (skyStatus.equals("CLOUDY") || skyStatus.equals("SUNNY")) {
            return RecommendKind.NORMAL;
        }

        return RecommendKind.NORMAL;
    }
}
