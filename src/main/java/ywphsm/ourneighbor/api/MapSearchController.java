package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.store.distance.Distance.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MapSearchController {

    private final StoreService storeService;

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

        // 리팩토링 : dto에 거리값 set 해주면 해결 (별도의 List 사용할 필요없음)
        calculateHowFarToTheTarget(myLat, myLon, dto);

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/get-top5-categories")
    public ResultClass<?> getTop5StoresByCategories(@RequestParam String categoryId,
                                            @CookieValue(value = "lat", required = false) String myLat,
                                            @CookieValue(value = "lon", required = false) String myLon) {
        double dist = 3;

        List<Store> findStores = storeService.getTop5ByCategories(categoryId, dist,
                Double.parseDouble(myLat), Double.parseDouble(myLon));

        List<SimpleSearchStoreDTO> dto = findStores.stream()
                .map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        calculateHowFarToTheTarget(myLat, myLon, dto);

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist).collect(Collectors.toList());

        return new ResultClass<>(result.size(), result);
    }
}