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
import ywphsm.ourneighbor.domain.store.StoreUtil;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.store.StoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.store.distance.Distance.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SearchController {

    private final StoreService storeService;

    private final CategoryService categoryService;

    @GetMapping("/search-by-keyword")
    public ResultClass<?> searchByKeyword(@RequestParam String keyword,
                                          @CookieValue(value = "lat", required = false) Double myLat,
                                          @CookieValue(value = "lon", required = false) Double myLon) {
        List<Store> findStores = storeService.searchByKeyword(keyword);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(StoreUtil::autoUpdateStatus);

        for (SimpleSearchStoreDTO dto : result) {
            log.info("dto={}", dto.getStatus());
        }

        if (!(myLat == null) && !(myLon == null)) {
            calculateHowFarToTheTarget(myLat, myLon, result);
        }

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/search-by-category")
    public ResultClass<?> searchByCategory(@RequestParam Long categoryId,
                                           @RequestParam double dist,
                                           @CookieValue(value = "lat", required = false) Double myLat,
                                           @CookieValue(value = "lon", required = false) Double myLon) {
        List<Store> findStores = storeService.searchByCategory(categoryId);

        List<SimpleSearchStoreDTO> dto = findStores.stream()
                .map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        dto.forEach(StoreUtil::autoUpdateStatus);

        if (!(myLat == null) && !(myLon == null)) {
            calculateHowFarToTheTarget(myLat, myLon, dto);
        }

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/search-topN-categories")
    public ResultClass<?> searchTopNStoresByCategories(@RequestParam Long categoryId,
                                                       @CookieValue(value = "lat", required = false) Double myLat,
                                                       @CookieValue(value = "lon", required = false) Double myLon) {
        if (myLat == null || myLon == null) {
            return new ResultClass<>(0, new ArrayList<>());
        }

        double dist = 3;

        List<Store> findStores = storeService.searchTopNByCategories(categoryId, dist, myLat, myLon);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(StoreUtil::autoUpdateStatus);

        calculateHowFarToTheTarget(myLat, myLon, result);

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/search-top7-random")
    public ResultClass<?> searchTop7Random(@CookieValue(value = "lat", required = false) Double myLat,
                                           @CookieValue(value = "lon", required = false) Double myLon) {
        final double dist = 3;
        List<SimpleSearchStoreDTO> result = storeService.searchTop7Random(myLat, myLon, dist);

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/recommend-post")
    public Slice<SimpleSearchStoreDTO> getRecommendStoreByHashtag(@RequestParam String hashtagIdList,
                                                                  @CookieValue(value = "lat", required = false) Double myLat,
                                                                  @CookieValue(value = "lon", required = false) Double myLon) {
        if (myLat == null || myLon == null) {
            return new SliceImpl<>(null, null, false);
        }

        final double dist = 3;

        List<Long> hashtagList = Arrays.stream(hashtagIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Slice<SimpleSearchStoreDTO> result = storeService.searchByHashtag(
                hashtagList, 0, myLat, myLon, dist);

//        result.forEach(StoreUtil::autoUpdateStatus);

        calculateHowFarToTheTarget(myLat, myLon, result.getContent());

        return result;
    }

    @GetMapping("/get-cate-images")
    public List<List<String>> getTopNStoresImagesByCategories(@CookieValue(value = "lat", required = false, defaultValue = "") Double myLat,
                                                              @CookieValue(value = "lon", required = false, defaultValue = "") Double myLon) {
        final Long depth = 1L;
        final double dist = 3;

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepthCaseByOrderByName(depth);

        List<List<String>> categoryImageList = new ArrayList<>();

        if (myLat != null && myLon != null) {
            for (CategoryDTO.Simple simple : rootCategoryList) {
                categoryImageList.add(storeService.getTopNImageByCategories(
                        (simple.getCategoryId()), dist, myLat, myLon));
            }
        }

        return categoryImageList;
    }

}