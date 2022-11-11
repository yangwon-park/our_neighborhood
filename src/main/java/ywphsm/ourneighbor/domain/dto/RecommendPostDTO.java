package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.RecommendPost;

public class RecommendPostDTO {

    @Data
    @NoArgsConstructor
    public static class Add {

        private String skyStatus;

        private String pm10Value;

        private String tmp;

        private String pop;

        private String header;

        private String content;

        @Builder
        public Add(String skyStatus, String pm10Value, String tmp, String pop, String header, String content) {
            this.skyStatus = skyStatus;
            this.pm10Value = pm10Value;
            this.tmp = tmp;
            this.pop = pop;
            this.header = header;
            this.content = content;
        }

        public RecommendPost toEntity() {
            return RecommendPost.builder()
                    .skyStatus(skyStatus)
                    .pm10Value(pm10Value)
                    .tmp(tmp)
                    .pop(pop)
                    .header(header)
                    .content(content)
                    .build();
        }

        public static RecommendPostDTO.Add of(RecommendPost entity) {
            return Add.builder()
                    .skyStatus(entity.getSkyStatus())
                    .pm10Value(entity.getPm10Value())
                    .tmp(entity.getTmp())
                    .pop(entity.getPop())
                    .header(entity.getHeader())
                    .content(entity.getContent())
                    .build();
        }
    }

}
