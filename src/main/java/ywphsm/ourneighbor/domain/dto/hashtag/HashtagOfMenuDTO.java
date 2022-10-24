package ywphsm.ourneighbor.domain.dto.hashtag;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;

import javax.validation.constraints.NotNull;


public class HashtagOfMenuDTO {

    @Data
    @NoArgsConstructor
    public static class Simple {

        @NotNull
        private Long id;

        private Long hashtagId;

        private String hashtagName;

        @Builder
        public Simple(Long id, Long hashtagId, String hashtagName) {
            this.id = id;
            this.hashtagId = hashtagId;
            this.hashtagName = hashtagName;
        }

        public Simple(HashtagOfMenu hashtagOfMenu) {
            id = hashtagOfMenu.getId();
            hashtagId = hashtagOfMenu.getHashtag().getId();
            hashtagName = hashtagOfMenu.getHashtag().getName();
        }
    }
}
