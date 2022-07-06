package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.StoreRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true) // 데이터 변경 X
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Long saveStore(Store store) {
        storeRepository.save(store);
        return store.getId();
    }

    public List<Store> findStores() {
        return storeRepository.findAll();
    }

    public Store findOne(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(NoSuchElementException::new);
    }

    public List<Store> findByName(String name) {
        return storeRepository.findByName(name);
    }

}
