package ywphsm.ourneighbor.repository.category;

import ywphsm.ourneighbor.domain.CategoryOfStore;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryCustom {

    CategoryOfStore findCategoryOfStore(Long storeId, Long categoryId);
}
