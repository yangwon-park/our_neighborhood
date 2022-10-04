package ywphsm.ourneighbor.repository.category;

import ywphsm.ourneighbor.domain.category.CategoryOfStore;

public interface CategoryRepositoryCustom {

    CategoryOfStore findCategoryOfStore(Long storeId, Long categoryId);
}

