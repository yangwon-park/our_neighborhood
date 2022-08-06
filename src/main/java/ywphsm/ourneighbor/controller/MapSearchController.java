package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapSearchController {

    private final StoreService storeService;




    // 검색한 스토어 리스트
    @GetMapping(value = "/searchStore", produces = "application/json;charset=utf-8")
    public List<Store> axios3(@RequestParam String keyword) {
        return storeService.searchByKeyword(keyword);
    }
}
