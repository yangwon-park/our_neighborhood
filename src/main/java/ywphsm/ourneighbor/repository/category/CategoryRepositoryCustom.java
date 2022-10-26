package ywphsm.ourneighbor.repository.category;

import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;

import java.util.List;

public interface CategoryRepositoryCustom {

    CategoryOfStore findCategoryOfStore(Long storeId, Long categoryId);

    List<Category> findTop4ByDepth(Long depth);
}

