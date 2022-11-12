package ywphsm.ourneighbor.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    Slice<Store> searchByHashtag(Long hashtagId, Pageable pageable);
}