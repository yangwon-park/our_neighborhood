package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.service.RecommendPostService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecommendApiController {

    private final RecommendPostService recommendPostService;

    @PostMapping("/admin/recommend-post")
    public Long saveRecommendPost(RecommendPostDTO.Add dto) {
        return recommendPostService.save(dto);
    }
}
