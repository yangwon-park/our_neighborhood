package ywphsm.ourneighbor.domain.dto.hashtag;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;

import javax.validation.constraints.NotNull;


public class HashtagOfStoreDTO {

    @Data
    @NoArgsConstructor
    public static class Detail {

        @NotNull
        private Long storeId;

        @NotNull
        private Long hashtagId;

        private String hashtagName;

        @Builder
        public Detail(Long storeId, Long hashtagId, String hashtagName) {
            this.storeId = storeId;
            this.hashtagId = hashtagId;
            this.hashtagName = hashtagName;
        }

        public Detail(HashtagOfStore hashtagOfStore) {
            storeId = hashtagOfStore.getStore().getId();
            hashtagId = hashtagOfStore.getHashtag().getId();
            hashtagName = hashtagOfStore.getHashtag().getName();
        }

        public static Detail of(HashtagOfStore entity) {
            return Detail.builder()
                    .storeId(entity.getStore().getId())
                    .hashtagId(entity.getHashtag().getId())
                    .hashtagName(entity.getHashtag().getName())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class WithCount {
        private Long hashtagId;
        private String hashtagName;
        private Long storeId;
        private Long count;

        @Builder
        public WithCount(Long hashtagId, String hashtagName, Long storeId, Long count) {
            this.hashtagId = hashtagId;
            this.hashtagName = hashtagName;
            this.storeId = storeId;
            this.count = count;
        }
    }

}