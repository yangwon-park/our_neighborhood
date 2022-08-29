package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.Category;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryDTO {

    private Long categoryId;
    private String name;
    private Long depth;

    private Long parent_id;
    private List<CategoryDTO> children;

    @Builder
    public CategoryDTO(Long categoryId, String name, Long depth, Long parent_id, List<CategoryDTO> children) {
        this.categoryId = categoryId;
        this.name = name;
        this.depth = depth;
        this.parent_id = parent_id;
        this.children = children;
    }

    // Entity를 DTO로 변환하는 메소드
    public static CategoryDTO of(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .depth(category.getDepth())
                .parent_id(category.getId())
                .children(category.getChildren().stream().map(CategoryDTO::of).collect(Collectors.toList()))
                .build();
    }
}
