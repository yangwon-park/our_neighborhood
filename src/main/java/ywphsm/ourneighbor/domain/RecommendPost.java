package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.api.dto.RecommendKind;

import javax.persistence.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecommendPost extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "recommend_post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RecommendKind recommendKind;

    private String header;

    private String content;

    @Builder
    public RecommendPost(Long id, RecommendKind recommendKind,
                         String header, String content) {
        this.id = id;
        this.recommendKind = recommendKind;
        this.header = header;
        this.content = content;
    }
}
