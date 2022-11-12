package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "recommendPost", fetch = FetchType.LAZY)
    private List<Hashtag> hashtagList = new ArrayList<>();


    @Builder
    public RecommendPost(Long id, RecommendKind recommendKind,
                         String header, String content,
                         List<Hashtag> hashtagList) {
        this.id = id;
        this.recommendKind = recommendKind;
        this.header = header;
        this.content = content;
        this.hashtagList = hashtagList;
    }


    /*
        === 연관 관계 편의 메소드 ===
     */
    public void addHashtag(Hashtag hashtag) {
        hashtag.setRecommendPost(this);
        hashtagList.add(hashtag);
    }
}
