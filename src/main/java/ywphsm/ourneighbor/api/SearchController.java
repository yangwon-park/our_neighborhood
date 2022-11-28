package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreUtil;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

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
                                          @CookieValue(value = "lat", required = false) String myLat,
                                          @CookieValue(value = "lon", required = false) String myLon) {
        List<Store> findStores = storeService.searchByKeyword(keyword);

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(StoreUtil::autoUpdateStatus);

        if (!myLat.isEmpty() && !myLon.isEmpty()) {
            calculateHowFarToTheTarget(myLat, myLon, result);
        }

        log.info("result={}", result);

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

        dto.forEach(StoreUtil::autoUpdateStatus);

        if (!myLat.isEmpty() && !myLon.isEmpty()) {
            calculateHowFarToTheTarget(myLat, myLon, dto);
        }

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        log.info("result={}", result);

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/search-topN-categories")
    public ResultClass<?> searchTopNStoresByCategories(@RequestParam Long categoryId,
                                                       @CookieValue(value = "lat", required = false) String myLat,
                                                       @CookieValue(value = "lon", required = false) String myLon) {
        double dist = 3;

        List<Store> findStores = storeService.searchTopNByCategories(categoryId, dist,
                Double.parseDouble(myLat), Double.parseDouble(myLon));

        List<SimpleSearchStoreDTO> result = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        result.forEach(StoreUtil::autoUpdateStatus);

        if (!myLat.isEmpty() && !myLon.isEmpty()) {
            calculateHowFarToTheTarget(myLat, myLon, result);
        }

        log.info("result={}", result);

        return new ResultClass<>(result.size(), result);
    }

    @GetMapping("/get-cate-images")
    public List<List<String>> getTopNStoresImagesByCategories(@CookieValue(value = "lat", required = false, defaultValue = "") String lat,
                                                              @CookieValue(value = "lon", required = false, defaultValue = "") String lon) {
        double dist = 3;

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepth(1L);

        List<List<String>> categoryImageList = new ArrayList<>();

        if (lat != null && lon != null) {
            for (CategoryDTO.Simple simple : rootCategoryList) {
                categoryImageList.add(storeService.getTopNImageByCategories(
                        (simple.getCategoryId()), dist, Double.parseDouble(lat), Double.parseDouble(lon)));
            }
        }

        return categoryImageList;
    }

    @GetMapping("/recommend-post")
    public Slice<SimpleSearchStoreDTO> getRecommendStoreByHashtag(String hashtagIdList,
                                                                  @CookieValue(value = "lat", required = false) String myLat,
                                                                  @CookieValue(value = "lon", required = false) String myLon) {
        double dist = 3;

        List<Long> hashtagList = Arrays.stream(hashtagIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Slice<SimpleSearchStoreDTO> result = storeService.searchByHashtag(
                hashtagList, 0, Double.parseDouble(myLat), Double.parseDouble(myLon), dist);

        result.forEach(StoreUtil::autoUpdateStatus);

        if (!myLat.isEmpty() && !myLon.isEmpty()) {
            calculateHowFarToTheTarget(myLat, myLon, result.getContent());
        }

        log.info("result={}", result.getContent());

        return result;
    }
}