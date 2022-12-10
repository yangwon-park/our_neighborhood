package ywphsm.ourneighbor.domain.dto.hashtag;

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

    // List 자체가 null로 들어오면 NPE 발생
    // 빈 ArrayList를 넣어줌으로써 해결
    public Hashtag toEntity() {
        return Hashtag.builder()
                .id(hashtagId)
                .name(name)
                .hashtagOfStoreList(new ArrayList<>())
                .hashtagOfMenuList(new ArrayList<>())
                .build();
    }

    public static HashtagDTO of(Hashtag entity) {
        return HashtagDTO.builder()
                .hashtagId(entity.getId())
                .name(entity.getName())
                .build();
    }
}
