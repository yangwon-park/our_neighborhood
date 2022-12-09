package ywphsm.ourneighbor.domain.dto.category;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.category.Category;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;


public class CategoryDTO {

    @Data
    @NoArgsConstructor
    public static class Detail {

        private Long categoryId;

        private String name;

        private Long depth;

        private Long parentId;

        private List<CategoryDTO.Detail> children;

        @Builder
        public Detail(Long categoryId, String name, Long depth,
                      Long parent_id, List<CategoryDTO.Detail> children) {
            this.categoryId = categoryId;
            this.name = name;
            this.depth = depth;
            this.parentId = parent_id;
            this.children = children;
        }

        public Category toEntity() {
            return Category.builder()
                    .name(name)
                    .depth(depth)
                    .build();
        }

        /*
            자식 Category를 고려하지 않고 Entity로 변환하고자 할 때 사용할 생성자 메소드
         */
        public Detail(Category category) {
            categoryId = category.getId();
            name = category.getName();
            depth = category.getDepth();

            if (category.getParent() == null) {
                parentId = 0L;
            } else {
                parentId = category.getParent().getId();
            }
        }

        /*
            Entity를 DTO로 변환하는 메소드
         */
        public static CategoryDTO.Detail of(Category entity) {
            if (entity.getParent() == null) {
                return CategoryDTO.Detail.builder()
                        .categoryId(entity.getId())
                        .name(entity.getName())
                        .depth(entity.getDepth())
                        .parent_id(0L)
                        .children(entity.getChildren().stream().map(CategoryDTO.Detail::of).collect(Collectors.toList()))
                        .build();
            }

            return CategoryDTO.Detail.builder()
                    .categoryId(entity.getId())
                    .name(entity.getName())
                    .depth(entity.getDepth())
                    .parent_id(entity.getParent().getId())
                    .children(entity.getChildren().stream().map(CategoryDTO.Detail::of).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Simple {

        private Long categoryId;

        @NotBlank
        private String name;

        @Builder
        public Simple(Long categoryId, String name) {
            this.categoryId = categoryId;
            this.name = name;
        }

        public static CategoryDTO.Simple of(Category entity) {
            return Simple
                    .builder()
                    .categoryId(entity.getId())
                    .name(entity.getName())
                    .build();
        }
    }

}