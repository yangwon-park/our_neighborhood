package ywphsm.ourneighbor.domain.hashtag;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.menu.Menu;

import javax.persistence.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HashtagOfMenu {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;


    /*
        생성자
     */
    @Builder
    public HashtagOfMenu(Long id, Hashtag hashtag, Menu menu) {
        this.id = id;
        this.hashtag = hashtag;
        this.menu = menu;
    }

    public HashtagOfMenu(Hashtag hashtag, Menu menu) {
        this.hashtag = hashtag;
        this.menu = menu;
    }


    /*
        === 연관 관계 편의 메소드 ===
    */

    // 아래 로직 (Category도 포함)
    // HashtagOfMenu Table엔 값이 persist 되지 않는다
    // (별도의 save를 하지 않기 때문)
    // save구문을 따로 만들지 않고 Store 쪽 연관 관계에서 CascadeType.PERSIST로 종속 관계를 부여하여
    // 좀 더 간결하게 로직을 처리했음
    public static void linkHashtagAndMenu(Hashtag hashtag, Menu menu) {
        HashtagOfMenu hashtagOfStore = new HashtagOfMenu(hashtag, menu);

        log.info("hashtagOfStore={}", hashtagOfStore.getHashtag());
        log.info("hashtagOfStore={}", hashtagOfStore.getMenu());

        hashtag.getHashtagOfMenuList().add(hashtagOfStore);
        menu.getHashtagOfMenuList().add(hashtagOfStore);
    }

    public void updateHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

}

