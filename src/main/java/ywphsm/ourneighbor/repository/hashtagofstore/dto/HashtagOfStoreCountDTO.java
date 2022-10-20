package ywphsm.ourneighbor.repository.hashtagofstore.dto;

import lombok.Data;

@Data
public class HashtagOfStoreCountDTO {

    private Long hashtagId;
    private Long storeId;
    private Long count;

    public HashtagOfStoreCountDTO(Long hashtagId, Long storeId, Long count) {
        this.hashtagId = hashtagId;
        this.storeId = storeId;
        this.count = count;
    }
}
