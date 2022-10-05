package ywphsm.ourneighbor.controller.form;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.category.Category;

@Data
public class CategorySimpleDTO {

    private Long categoryId;
    private String name;

    @Builder
    public CategorySimpleDTO(Long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public static CategorySimpleDTO of(Category entity) {
        return CategorySimpleDTO.builder()
                .categoryId(entity.getId())
                .name(entity.getName())
                .build();
    }
}
