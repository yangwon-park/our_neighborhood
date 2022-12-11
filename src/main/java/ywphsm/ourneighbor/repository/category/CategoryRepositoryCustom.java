package ywphsm.ourneighbor.repository.category;

import ywphsm.ourneighbor.domain.category.Category;

import java.util.List;

public interface CategoryRepositoryCustom {

    List<Category> findByDepthCaseByOrderByName(Long depth);

    void deleteByCategoryLinkedCategoryOfStore(Category category);
}

