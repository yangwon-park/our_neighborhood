package ywphsm.ourneighbor.domain.hashtag;

import lombok.*;

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

    /*
        생성자
     */
    @Builder
    public Hashtag(Long id, String name, List<HashtagOfStore> hashtagOfStoreList) {
        this.id = id;
        this.name = name;
        this.hashtagOfStoreList = hashtagOfStoreList;
    }

    public Hashtag(String name, List<HashtagOfStore> hashtagOfStoreList) {
        this.name = name;
        this.hashtagOfStoreList = hashtagOfStoreList;
    }
}

