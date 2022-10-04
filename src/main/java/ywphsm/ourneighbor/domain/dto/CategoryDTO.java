package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.category.Category;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CategoryDTO {

    private Long categoryId;

<<<<<<< HEAD
    //    @NotBlank
=======
//    @NotBlank
>>>>>>> main
    private String name;
    private Long depth;

    private Long parentId;
    private List<CategoryDTO> children;



    @Builder
    public CategoryDTO(Long categoryId, String name, Long depth, Long parent_id, List<CategoryDTO> children) {
        this.categoryId = categoryId;
        this.name = name;

        this.depth = depth;
        this.parentId = parent_id;
        this.children = children;
    }

    public CategoryDTO(Category category) {
        categoryId = category.getId();
        name = category.getName();
        depth = category.getDepth();

        if (category.getParent() == null) {
            parentId = 0L;
        } else {
            parentId = category.getParent().getId();
        }
    }

    public Category toEntity() {
        return Category.builder()
                .name(name)
                .depth(depth)
                .build();
    }

    // Entity를 DTO로 변환하는 메소드
    public static CategoryDTO of(Category entity) {

        if (entity.getParent() == null) {
            return CategoryDTO.builder()
                    .categoryId(entity.getId())
                    .name(entity.getName())
                    .depth(entity.getDepth())
                    .parent_id(0L)
                    .children(entity.getChildren().stream().map(CategoryDTO::of).collect(Collectors.toList()))
                    .build();
        }

        return CategoryDTO.builder()
                .categoryId(entity.getId())
                .name(entity.getName())
                .depth(entity.getDepth())
                .parent_id(entity.getParent().getId())
                .children(entity.getChildren().stream().map(CategoryDTO::of).collect(Collectors.toList()))
                .build();
    }
}