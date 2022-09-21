package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.CategoryOfStore;

import javax.validation.constraints.NotNull;

@Data
public class CategoryOfStoreDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private Long categoryId;

    @NotNull
    private String categoryName;

    @Builder
    public CategoryOfStoreDTO(Long storeId, Long categoryId, String categoryName) {
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public CategoryOfStoreDTO(CategoryOfStore categoryOfStore) {
        this.storeId = categoryOfStore.getStore().getId();
        this.categoryId = categoryOfStore.getCategory().getId();
        this.categoryName = categoryOfStore.getCategory().getName();
    }

    public static CategoryOfStoreDTO of(CategoryOfStore entity) {
        return CategoryOfStoreDTO.builder()
                .storeId(entity.getStore().getId())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .build();
    }
}
