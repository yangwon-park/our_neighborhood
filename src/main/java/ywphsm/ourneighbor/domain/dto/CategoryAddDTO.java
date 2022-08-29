package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.Category;

@Data
public class CategoryAddDTO {

    private Long categoryId;
    private String name;
    private Long depth;
    private Long parent_id;

    @Builder
    public CategoryAddDTO(Long categoryId, String name, Long depth, Long parent_id) {
        this.categoryId = categoryId;
        this.name = name;
        this.depth = depth;
        this.parent_id = parent_id;
    }

    public Category toEntity() {
        return Category.builder()
                .name(name)
                .depth(depth)
                .build();
    }
}
