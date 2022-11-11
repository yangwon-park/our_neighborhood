package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.repository.recommendpost.RecommendPostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;

    @Transactional
    public Long save(RecommendPostDTO.Add dto) {
        RecommendPost recommendPost = dto.toEntity();

        return recommendPostRepository.save(recommendPost).getId();
    }

    public RecommendPostDTO.Simple getRecommendPost(String skyStatus, String pm10Value,
                                                    String tmp, String pcp) {

        RecommendKind cond = getRecommendKind(skyStatus, pm10Value, tmp, pcp);

        List<RecommendPost> list = recommendPostRepository.findByRecommendKind(cond);

        List<RecommendPostDTO.Simple> collect = list.stream()
                .map(RecommendPostDTO.Simple::of)
                .collect(Collectors.toList());

        return collect.get(0);
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

        return null;
    }
}
