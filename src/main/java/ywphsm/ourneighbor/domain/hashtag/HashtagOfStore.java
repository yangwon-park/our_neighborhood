package ywphsm.ourneighbor.domain.hashtag;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HashtagOfStore {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    
    /*
        생성자
     */
    @Builder
    public HashtagOfStore(Long id, Hashtag hashtag, Store store) {
        this.id = id;
        this.hashtag = hashtag;
        this.store = store;
    }

    public HashtagOfStore(Hashtag hashtag, Store store) {
        this.hashtag = hashtag;
        this.store = store;
    }


    /*
        === 연관 관계 편의 메소드 ===
    */

    // 아래 로직 (Category도 포함)
    // HashtagOfStore Table엔 값이 persist 되지 않는다
    // (별도의 save를 하지 않기 때문)
    // save구문을 따로 만들지 않고 Store 쪽 연관 관계에서 CascadeType.PERSIST로 종속 관계를 부여하여
    // 좀 더 간결하게 로직을 처리했음
    public static void linkHashtagAndStore(Hashtag hashtag, Store store) {
        HashtagOfStore hashtagOfStore = new HashtagOfStore(hashtag, store);

        log.info("hashtagOfStore={}", hashtagOfStore.getHashtag());
        log.info("hashtagOfStore={}", hashtagOfStore.getStore());

        hashtag.getHashtagOfStoreList().add(hashtagOfStore);
        store.getHashtagOfStoreList().add(hashtagOfStore);
    }

}

