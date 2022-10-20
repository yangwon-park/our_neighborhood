package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;

import javax.validation.constraints.NotNull;

@Data
public class HashtagOfStoreDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private Long hashtagId;

    private String hashtagName;

    @Builder
    public HashtagOfStoreDTO(Long storeId, Long hashtagId, String hashtagName) {
        this.storeId = storeId;
        this.hashtagId = hashtagId;
        this.hashtagName = hashtagName;
    }

    public HashtagOfStoreDTO(HashtagOfStore hashtagOfStore) {
        storeId = hashtagOfStore.getStore().getId();
        hashtagId = hashtagOfStore.getHashtag().getId();
        hashtagName = hashtagOfStore.getHashtag().getName();
    }
}