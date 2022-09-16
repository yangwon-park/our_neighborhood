package ywphsm.ourneighbor.domain.dto;

import lombok.Data;
import ywphsm.ourneighbor.domain.CategoryOfStore;

@Data
public class CategoryOfStoreDTO {

    private Long storeId;
    private Long categoryId;

    public CategoryOfStoreDTO(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryOfStoreDTO(CategoryOfStore categoryOfStore) {
        this.storeId = categoryOfStore.getStore().getId();
        this.categoryId = categoryOfStore.getCategory().getId();
    }
}
