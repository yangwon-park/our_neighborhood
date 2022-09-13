package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.distance.Distance;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MapSearchController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    // 메뉴 리스트는 불러오지 않음
    // 단순 조회이므로 fetch 조인으로 최적화
    // map.html에서 가게 이름을 검색하는 경우 수행됨
    @GetMapping(value = "/searchStores", produces = "application/json;charset=utf-8")
    public ResultClass<?> searchStores(@RequestParam String keyword,
                                       @CookieValue(value = "lat", required = false) String myLat,
                                       @CookieValue(value = "lon", required = false) String myLon) {
        List<Store> findStores = storeService.searchByKeyword(keyword);

        List<SimpleSearchStoreDTO> findDTO = findStores.stream()
                .map(SimpleSearchStoreDTO::new)
                .collect(Collectors.toList());

        findDTO.forEach(dto -> {
            double dist = Distance.byHaversine(dto.getLat(), dto.getLon(),
                    Double.parseDouble(myLat), Double.parseDouble(myLon));

            double refineDist = Math.ceil(dist * 10) / 10.0;

            dto.setDistance(refineDist);

        });

        List<SimpleSearchStoreDTO> result = new ArrayList<>();

        for (SimpleSearchStoreDTO dto : findDTO) {
            if (dto.getDistance() < 3.5) {
                result.add(dto);
            }
        }

        return new ResultClass<>(result.size(), result);
    }
}
