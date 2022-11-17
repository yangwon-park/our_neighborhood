package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.RecommendPostService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecommendApiController {

    private final RecommendPostService recommendPostService;

    private final StoreService storeService;

    @GetMapping("/recommend-post")
    public Slice<SimpleSearchStoreDTO> getRecommendPost(String hashtagIdList,
                                                        @CookieValue(value = "lat", required = false) String myLat,
                                                        @CookieValue(value = "lon", required = false) String myLon) throws org.locationtech.jts.io.ParseException {
        List<Long> result = Arrays.stream(hashtagIdList.split(","))
                                    .map(Long::parseLong)
                                    .collect(Collectors.toList());

        log.info("result = {}", storeService.searchByHashtag(result, 0, Double.parseDouble(myLat), Double.parseDouble(myLon)).getContent());

        return storeService.searchByHashtag(result, 0, Double.parseDouble(myLat), Double.parseDouble(myLon));
    }

    @PostMapping("/admin/recommend-post")
    public Long saveRecommendPost(RecommendPostDTO.Add dto) throws ParseException {
        return recommendPostService.save(dto);
    }
}
