package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.domain.store.StoreUtil;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.store.StoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.store.StoreUtil.*;
import static ywphsm.ourneighbor.domain.store.distance.Distance.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SearchController {

    private final StoreService storeService;

    private final CategoryService categoryService;

    private static final double DIST_TO_TARGET = 3;

    /*
        map.html에서 검색창에 조회 시 동작하는 API
     */
    @GetMapping("/search-by-keyword")
    public ResultClass<?> searchByKeyword(@RequestParam String keyword,
                                          @CookieValue(value = "lat", required = false) Double myLat,
                                          @CookieValue(value = "lon", required = false) Double myLon) {
        List<Store> findStores = storeService.searchByKeyword(keyword);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(simpleSearchStoreDTO ->
                simpleSearchStoreDTO.setStatus(
                        autoUpdateStatus(simpleSearchStoreDTO.getBusinessTime(), simpleSearchStoreDTO.getOffDays())));

        for (SimpleSearchStoreDTO dto : result) {
            log.info("dto={}", dto.getStatus());
        }

        if (!(myLat == null) && !(myLon == null)) {
            calculateHowFarToTheTarget(myLat, myLon, result);
        }

        return new ResultClass<>(result.size(), result);
    }

    /*
        map.html에서 카테고리를 선택하여 조회 시 동작하는 API
        거리 조회 기능 리팩토링해야 함
     */
    @GetMapping("/search-by-category")
    public ResultClass<?> searchByCategory(@RequestParam Long categoryId,
                                           @RequestParam double dist,
                                           @CookieValue(value = "lat", required = false) Double myLat,
                                           @CookieValue(value = "lon", required = false) Double myLon) {
        List<Store> findStores = storeService.searchByCategory(categoryId);

        List<SimpleSearchStoreDTO> dto = findStores.stream()
                .map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        dto.forEach(simpleSearchStoreDTO ->
                simpleSearchStoreDTO.setStatus(
                        autoUpdateStatus(simpleSearchStoreDTO.getBusinessTime(), simpleSearchStoreDTO.getOffDays())));

        if (!(myLat == null) && !(myLon == null)) {
            calculateHowFarToTheTarget(myLat, myLon, dto);
        }

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        return new ResultClass<>(result.size(), result);
    }

    /*
        주변 인기 스팟 추천 API (카테고리)
     */
    @GetMapping("/search-topN-categories")
    public ResultClass<?> searchTopNStoresByCategories(@RequestParam Long categoryId,
                                                       @CookieValue(value = "lat", required = false) Double myLat,
                                                       @CookieValue(value = "lon", required = false) Double myLon) {
        if (myLat == null || myLon == null) {
            return new ResultClass<>(0, new ArrayList<>());
        }

        List<Store> findStores = storeService.searchTopNByCategories(categoryId, DIST_TO_TARGET, myLat, myLon);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(simpleSearchStoreDTO ->
                simpleSearchStoreDTO.setStatus(
                        autoUpdateStatus(simpleSearchStoreDTO.getBusinessTime(), simpleSearchStoreDTO.getOffDays())));

        calculateHowFarToTheTarget(myLat, myLon, result);

        return new ResultClass<>(result.size(), result);
    }

    /*
        주변 인기 스팟 추천 이미지 조회 API
     */
    @GetMapping("/get-cate-images")
    public List<List<String>> getTopNStoresImagesByCategories(@CookieValue(value = "lat", required = false, defaultValue = "") Double myLat,
                                                              @CookieValue(value = "lon", required = false, defaultValue = "") Double myLon) {
        final Long DEPTH = 1L;

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepthCaseByOrderByName(DEPTH);

        List<List<String>> categoryImageList = new ArrayList<>();

        if (myLat != null && myLon != null) {
            for (CategoryDTO.Simple simple : rootCategoryList) {
                categoryImageList.add(storeService.getTopNImageByCategories(
                        (simple.getCategoryId()), DIST_TO_TARGET, myLat, myLon));
            }
        }

        return categoryImageList;
    }

    /*
        날씨에 어울리는 스팟 추천 API
     */
    @GetMapping("/recommend-post")
    public Slice<SimpleSearchStoreDTO> getRecommendStoreByHashtag(@RequestParam String hashtagIdList,
                                                                  @CookieValue(value = "lat", required = false) Double myLat,
                                                                  @CookieValue(value = "lon", required = false) Double myLon) {
        if (myLat == null || myLon == null) {
            return new SliceImpl<>(null, null, false);
        }

        List<Long> hashtagList = Arrays.stream(hashtagIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Slice<SimpleSearchStoreDTO> result = storeService.searchByHashtag(
                hashtagList, 0, myLat, myLon, DIST_TO_TARGET);

        result.forEach(simpleSearchStoreDTO ->
                simpleSearchStoreDTO.setStatus(
                        autoUpdateStatus(simpleSearchStoreDTO.getBusinessTime(), simpleSearchStoreDTO.getOffDays())));

        calculateHowFarToTheTarget(myLat, myLon, result.getContent());

        return result;
    }

    /*
        무작위 랜덤 추천 API
     */
    @GetMapping("/search-top7-random")
    public ResultClass<?> searchTop7Random(@CookieValue(value = "lat", required = false) Double myLat,
                                           @CookieValue(value = "lon", required = false) Double myLon) {
        List<SimpleSearchStoreDTO> result = storeService.searchTop7Random(myLat, myLon, DIST_TO_TARGET);

        return new ResultClass<>(result.size(), result);
    }
}