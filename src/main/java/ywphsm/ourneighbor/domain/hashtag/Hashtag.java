package ywphsm.ourneighbor.domain.hashtag;

import lombok.*;
import ywphsm.ourneighbor.domain.RecommendPost;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(of = {"id", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Hashtag {

    @Id
    @GeneratedValue
    @Column(name = "hashtag_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.PERSIST)
    private List<HashtagOfStore> hashtagOfStoreList = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.PERSIST)
    private List<HashtagOfMenu> hashtagOfMenuList = new ArrayList<>();

    // RecommendPost가 Persist되기 전에 Hashtag를 추가하려고 하면
    // 영속성 컨텍스트가 관리하지 않는 단계에 RecommendPost Entity가 놓임
    // 따라서 org.hibernate.TransientPropertyValueException 에러가 발생함
    // 이를 방지하기 위해 엔티티 생성 시점에 persist를 강제로 할지 cascade 옵션을 사용할지 고민 끝에
    // cascade.PERSIST 옵션을 사용하기로 결정
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recommend_post_id")
    private RecommendPost recommendPost;

    public void setRecommendPost(RecommendPost recommendPost) {
        this.recommendPost = recommendPost;
    }


    /*
        생성자
     */
    @Builder
    public Hashtag(Long id, String name, List<HashtagOfStore> hashtagOfStoreList,
                   List<HashtagOfMenu> hashtagOfMenuList, RecommendPost recommendPost) {
        this.id = id;
        this.name = name;
        this.hashtagOfStoreList = hashtagOfStoreList;
        this.hashtagOfMenuList = hashtagOfMenuList;
        this.recommendPost = recommendPost;
    }
}

