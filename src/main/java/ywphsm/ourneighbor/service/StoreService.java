package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.CategoryOfStore;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true) // 데이터 변경 X
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    // 매장 등록
    @Transactional
    public Long save(StoreDTO.Add dto, List<Category> categoryList) {

        Store store = dto.toEntity();

        for (Category category : categoryList) {
            CategoryOfStore categoryOfStore = CategoryOfStore.linkCategoryAndStore(category, store);
            log.info("categoryOfStore={}", categoryOfStore.getCategory().getName());
            log.info("categoryOfStore={}", categoryOfStore.getStore().getName());
        }

        // default: OPEN
        store.updateStatus(StoreStatus.OPEN);

        storeRepository.save(store);
        return store.getId();
    }


    @Transactional
    public Long update(Long storeId, StoreDTO.Update dto, List<Long> categoryIdList) {

        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        // 먼저 카테고리를 업데이트
        List<CategoryOfStore> categoryOfStoreList = findStore.getCategoryOfStoreList();

        for (int i = 0; i < categoryOfStoreList.size(); i++) {
            Long categoryId = categoryIdList.get(i);

            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 카테고리입니다. id = " + categoryId));
            categoryOfStoreList.get(i).updateCategory(category);
        }

        // 그 후, dto로 전달받은 수정된 정보를 별도로 업데이트 시킴
        findStore.update(dto.toEntity());

        return storeId;
    }

    @Transactional
    public Long delete(Long storeId) {

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 매장이 없습니다. id = " + storeId));

        storeRepository.delete(store);

        return storeId;
    }

    // 매장 하나 조회
    public Store findById(Long storeId) {
        Store store = storeRepository.findByIdWithFetch(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + storeId));

        store.autoUpdateStatus(store.getOffDays(), store.getBusinessTime());

        return store;
    }

    // 전체 매장 조회
    public List<Store> findStores() {
        return storeRepository.findAll();
    }

    // 검색어 포함 매장명 조회
    public List<Store> searchByKeyword(String keyword) {
        List<Store> stores = storeRepository.searchByKeyword(keyword);

        for (Store store : stores) {
            store.autoUpdateStatus(store.getOffDays(), store.getBusinessTime());
        }

        return stores;
    }

    public List<Store> searchByCategory(Long categoryId) {
        return storeRepository.searchByCategory(categoryId);
    }

}
