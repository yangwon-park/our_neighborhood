package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true) // 데이터 변경 X
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    // 매장 등록
    @Transactional
    public Long saveStore(Store store) {
        storeRepository.save(store);
        return store.getId();
    }

    // 전체 매장 조회
    public List<Store> findStores() {
        return storeRepository.findAll();
    }

    // 매장 하나 조회
    public Store findOne(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(NoSuchElementException::new);

        store.autoUpdateStatus(store.getOffDays(), store.getOpeningTime(), store.getClosingTime(), store.getBreakStart(), store.getBreakEnd());

        return store;
    }

    // 매장 이름으로 조회
    public List<Store> findByName(String name) {
        return storeRepository.findByName(name);
    }


    // 검색어 포함 매장명 조회
    public List<Store> searchByKeyword(String keyword) {
        List<Store> stores = storeRepository.searchByKeyword(keyword);

        for (Store store : stores) {
            store.autoUpdateStatus(store.getOffDays(), store.getOpeningTime(), store.getClosingTime(), store.getBreakStart(), store.getBreakEnd());
        }

        return stores;
    }

}
