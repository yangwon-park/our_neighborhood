package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.RecommendPostService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.store.distance.Distance.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MapSearchController {

    private final StoreService storeService;

    private final RecommendPostService recommendPostService;

    // 메뉴 리스트는 불러오지 않음
    // 단순 조회이므로 fetch 조인으로 최적화
    // map.html에서 가게 이름을 검색하는 경우 수행됨
    @GetMapping("/search-by-keyword")
    public ResultClass<?> searchByKeyword(@RequestParam String keyword,
                                          @CookieValue(value = "lat", required = false) String myLat,
                                          @CookieValue(value = "lon", required = false) String myLon) {
        List<Store> findStores = storeService.searchByKeyword(keyword);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        calculateHowFarToTheTarget(myLat, myLon, result);

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/search-by-category")
    public ResultClass<?> searchByCategory(@RequestParam String categoryId,
                                           @RequestParam double dist,
                                           @CookieValue(value = "lat", required = false) String myLat,
                                           @CookieValue(value = "lon", required = false) String myLon) {

        List<Store> findStores = storeService.searchByCategory(Long.parseLong(categoryId));

        List<SimpleSearchStoreDTO> dto = findStores.stream()
                .map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        if (!myLat.isEmpty() && !myLon.isEmpty()) {
            // 리팩토링 : dto에 거리값 set 해주면 해결 (별도의 List 사용할 필요없음)
            calculateHowFarToTheTarget(myLat, myLon, dto);
        }

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/get-topN-categories")
    public ResultClass<?> getTopNStoresByCategories(@RequestParam Long categoryId,
                                                    @CookieValue(value = "lat", required = false) String myLat,
                                                    @CookieValue(value = "lon", required = false) String myLon) throws ParseException {
        double dist = 3;

        List<Store> findStores = storeService.getTopNByCategories(categoryId, dist,
                Double.parseDouble(myLat), Double.parseDouble(myLon));

        List<SimpleSearchStoreDTO> dto = findStores.stream()
                .map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        // DTO에 거리값을 세팅해줌
        calculateHowFarToTheTarget(myLat, myLon, dto);

        return new ResultClass<>(dto.size(), dto);
    }


    @GetMapping("/get-recommend-post")
    public RecommendPostDTO.Simple getRecommendPost(@CookieValue(value = "skyStatus", required = false) String skyStatus,
                                             @CookieValue(value = "pm10Value", required = false) String pm10Value,
                                             @CookieValue(value = "tmp", required = false) String tmp,
                                             @CookieValue(value = "pcp", required = false) String pcp) {

        return recommendPostService.getRecommendPost(skyStatus, pm10Value, tmp, pcp.replace("m", ""));
    }

    @GetMapping("/get-store-based-weather")
    public ResultClass<?> getStoreBasedOnWeather(@CookieValue(value = "skyStatus", required = false) String skyStatus,
                                                 @CookieValue(value = "pm10Value", required = false) String pm10Value,
                                                 @CookieValue(value = "tmp", required = false) String tmp,
                                                 @CookieValue(value = "pop", required = false) String pop) {


        return null;
    }
}