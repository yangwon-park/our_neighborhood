package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RecommendPostDTO {

    @Data
    @NoArgsConstructor
    public static class Add {

        @NotBlank
        private String header;

        @NotBlank
        private String content;

        @NotNull
        private RecommendKind recommendKind;

        @Builder
        public Add(String header, String content ,RecommendKind recommendKind) {
            this.header = header;
            this.content = content;
            this.recommendKind = recommendKind;
        }

        public RecommendPost toEntity() {
            return RecommendPost.builder()
                    .header(header)
                    .content(content)
                    .recommendKind(recommendKind)
                    .build();
        }

        public static RecommendPostDTO.Add of(RecommendPost entity) {
            return Add.builder()
                    .header(entity.getHeader())
                    .content(entity.getContent())
                    .recommendKind(entity.getRecommendKind())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Simple {

        private String header;

        private String content;

        private RecommendKind recommendKind;

        @Builder
        public Simple(String header, String content, RecommendKind recommendKind) {
            this.header = header;
            this.content = content;
            this.recommendKind = recommendKind;
        }

        public RecommendPost toEntity() {
            return RecommendPost.builder()
                    .header(header)
                    .content(content)
                    .recommendKind(recommendKind)
                    .build();
        }

        public static RecommendPostDTO.Simple of(RecommendPost entity) {
            return Simple.builder()
                    .header(entity.getHeader())
                    .content(entity.getContent())
                    .recommendKind(entity.getRecommendKind())
                    .build();
        }
    }
}
