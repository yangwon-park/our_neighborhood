package ywphsm.ourneighbor.repository.store;

import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> searchByName(StoreSearchCond cond);

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    List<Store> getTop5ByCategories(String categoryId, double dist, double lat, double lon);

}