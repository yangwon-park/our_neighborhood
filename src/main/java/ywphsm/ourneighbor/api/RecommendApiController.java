package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.service.RecommendPostService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecommendApiController {

    private final RecommendPostService recommendPostService;

    @GetMapping("/get-recommend-post")
    public RecommendPostDTO.Simple getRecommendPost(@CookieValue(value = "skyStatus", required = false) String skyStatus,
                                                    @CookieValue(value = "pm10Value", required = false) String pm10Value,
                                                    @CookieValue(value = "tmp", required = false) String tmp,
                                                    @CookieValue(value = "pcp", required = false) String pcp) {
        return recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp.replace("m", ""));
    }

    @PostMapping("/admin/recommend-post")
    public Long saveRecommendPost(RecommendPostDTO.Add dto) throws ParseException {
        return recommendPostService.save(dto);
    }
}
