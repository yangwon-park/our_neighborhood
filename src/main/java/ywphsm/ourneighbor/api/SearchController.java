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
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.RecommendPostService;
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

    @GetMapping("/get-cate-images")
    public List<List<String>> getTopNStoresImagesByCategories(@CookieValue(value = "lat", required = false, defaultValue = "") String lat,
                                                              @CookieValue(value = "lon", required = false, defaultValue = "") String lon) throws ParseException {

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepth(1L);

        List<List<String>> categoryImageList = new ArrayList<>();

        double dist = 3;

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
                                                                  @CookieValue(value = "lon", required = false) String myLon) throws org.locationtech.jts.io.ParseException {
        List<Long> hashtagList = Arrays.stream(hashtagIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Slice<SimpleSearchStoreDTO> result = storeService.searchByHashtag(hashtagList, 0, Double.parseDouble(myLat), Double.parseDouble(myLon));

        calculateHowFarToTheTarget(myLat, myLon, result.getContent());

        return result;
    }
}