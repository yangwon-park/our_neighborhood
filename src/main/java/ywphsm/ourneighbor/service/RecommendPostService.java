package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.recommendpost.RecommendPostRepository;

import java.util.List;

import static ywphsm.ourneighbor.domain.hashtag.HashtagUtil.getHashtagNameList;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;

    private final HashtagRepository hashtagRepository;

    @Transactional
    public Long save(RecommendPostDTO.Add dto) throws ParseException {
        RecommendPost entity = dto.toEntity();

        if (!dto.getHashtag().isEmpty()) {
            List<String> hashtagNameList = getHashtagNameList(dto.getHashtag());

            for (String name : hashtagNameList) {
                boolean duplicateCheck = hashtagRepository.existsByName(name);

                Hashtag newHashtag;

                if (!duplicateCheck) {
                    HashtagDTO hashtagDTO = HashtagDTO.builder()
                            .name(name)
                            .build();

                    newHashtag = hashtagRepository.save(hashtagDTO.toEntity());
                } else {
                    newHashtag = hashtagRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다."));
                }

                entity.addHashtag(newHashtag);
            }
        }

        return recommendPostRepository.save(entity).getId();
    }

    public RecommendPostDTO.Simple getRecommendPost(String skyStatus, String pm10Value,
                                                    String tmp, String pcp) {
        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        /*   참고 (페이징 기능을 활용한 랜덤으로 게시글 조회하는 방법)
             https://stackoverflow.com/questions/24279186/fetch-random-records-using-spring-data-jpa
             https://kapentaz.github.io/jpa/Spring-Data-JPA-%EC%B9%B4%EC%9A%B4%ED%8A%B8-%EC%BF%BC%EB%A6%AC-%EC%97%86%EC%9D%B4-%ED%8E%98%EC%9D%B4%EC%A7%95-%EC%A1%B0%ED%9A%8C%ED%95%98%EA%B8%B0/#
        */
        Long count = recommendPostRepository.countByRecommendKind(cond);
        PageRequest pageRequest;

        if (count != 0L) {              // 조건에 맞는 추천 양식이 있다면 (count가 0이 아니다) 그 중 1개를 랜덤으로 추천
            int idx = (int) (Math.random() * count);
            pageRequest = PageRequest.of(idx, 1);

            List<RecommendPost> result = recommendPostRepository.findByRecommendKind(cond, pageRequest);

            return RecommendPostDTO.Simple.of(result.get(0));
        } else {                        // 없으면 NORMAL 중 하나 추천
            Long normalCnt = recommendPostRepository.countByRecommendKind(RecommendKind.NORMAL);
            int idx = (int) (Math.random() * normalCnt);
            pageRequest = PageRequest.of(idx, 1);

            return RecommendPostDTO.Simple.of(recommendPostRepository.findByRecommendKind(RecommendKind.NORMAL, pageRequest).get(0));
        }
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
            return RecommendKind.SUNNYANDGOODPM;
        }

        // 별 특색없는 하루
        if (skyStatus.equals("CLOUDY") || skyStatus.equals("SUNNY")) {
            return RecommendKind.NORMAL;
        }

        return RecommendKind.NORMAL;
    }
}
