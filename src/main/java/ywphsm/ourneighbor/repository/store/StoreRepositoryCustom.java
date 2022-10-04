package ywphsm.ourneighbor.repository.store;

import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface StoreRepositoryCustom {

    List<Store> searchByName(StoreSearchCond cond);

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    Optional<Store> findByIdWithFetch(Long storeId);
}