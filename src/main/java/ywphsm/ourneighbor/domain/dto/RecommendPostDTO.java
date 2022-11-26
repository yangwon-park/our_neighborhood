package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        @NotNull
        private String hashtag;

        @Builder
        public Add(String header, String content,
                   RecommendKind recommendKind, String hashtag) {
            this.header = header;
            this.content = content;
            this.recommendKind = recommendKind;
            this.hashtag = hashtag;
        }

        public RecommendPost toEntity() {
            return RecommendPost.builder()
                    .header(header)
                    .content(content)
                    .recommendKind(recommendKind)
                    .hashtagList(new ArrayList<>())
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

        private List<Long> hashtagIdList;

        @Builder
        public Simple(String header, String content, RecommendKind recommendKind, List<Long> hashtagIdList) {
            this.header = header;
            this.content = content;
            this.recommendKind = recommendKind;
            this.hashtagIdList = hashtagIdList;
        }

        public RecommendPost toEntity() {
            return RecommendPost.builder()
                    .header(header)
                    .content(content)
                    .recommendKind(recommendKind)
                    .hashtagList(new ArrayList<>())
                    .build();
        }

        public static RecommendPostDTO.Simple of(RecommendPost entity) {
            return Simple.builder()
                    .header(entity.getHeader())
                    .content(entity.getContent())
                    .recommendKind(entity.getRecommendKind())
                    .hashtagIdList(entity.getHashtagList().stream().map(Hashtag::getId).collect(Collectors.toList()))
                    .build();
        }
    }
}
