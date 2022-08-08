package ywphsm.ourneighbor.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleStoreDTO;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class MapSearchController {

    private final StoreService storeService;

    // 검색한 스토어 리스트 axios로 비동기 통신
    
    // 메뉴 리스트는 불러오지 않음
    // 단순 조회이므로 fetch 조인으로 최적화
    @GetMapping(value = "/searchStores", produces = "application/json;charset=utf-8")
    public Result searchStores(@RequestParam String keyword) {
        List<Store> findStores = storeService.searchByKeyword(keyword);
        List<SimpleStoreDTO> collect = findStores.stream()
                .map(SimpleStoreDTO::new)
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

}
