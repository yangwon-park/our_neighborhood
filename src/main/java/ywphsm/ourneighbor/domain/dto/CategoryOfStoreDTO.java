package ywphsm.ourneighbor.domain.dto;

import lombok.Data;
import ywphsm.ourneighbor.domain.CategoryOfStore;

import javax.validation.constraints.NotNull;

@Data
public class CategoryOfStoreDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private Long categoryId;

    public CategoryOfStoreDTO(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryOfStoreDTO(CategoryOfStore categoryOfStore) {
        this.storeId = categoryOfStore.getStore().getId();
        this.categoryId = categoryOfStore.getCategory().getId();
    }
}
