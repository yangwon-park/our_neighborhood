package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

import java.util.ArrayList;

@Data
@ToString
@NoArgsConstructor
public class HashtagDTO {

    private Long hashtagId;

    private String name;

    @Builder
    public HashtagDTO(Long hashtagId, String name) {
        this.hashtagId = hashtagId;
        this.name = name;
    }

    public HashtagDTO(Hashtag hashtag) {
        hashtagId = hashtag.getId();
        name = hashtag.getName();
    }

    public Hashtag toEntity() {
        return Hashtag.builder()
                .name(name)
                .hashtagOfStoreList(new ArrayList<>())
                .build();
    }

    public static HashtagDTO of(Hashtag entity) {
        return HashtagDTO.builder()
                .hashtagId(entity.getId())
                .name(entity.getName())
                .build();
    }
}
