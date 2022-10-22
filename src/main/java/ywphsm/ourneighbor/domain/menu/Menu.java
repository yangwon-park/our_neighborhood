package ywphsm.ourneighbor.domain.menu;

import lombok.*;
import ywphsm.ourneighbor.domain.BaseEntity;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {
        "id", "name", "price", "discountPrice",
        "discountStart", "discountEnd", "type"
})
@Entity
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "menu_id")
    private Long id;

    private String name;

    private Integer price;

    private int discountPrice;

    private LocalDateTime discountStart;

    private LocalDateTime discountEnd;

    @Enumerated(EnumType.STRING)
    private MenuType type;

    @Enumerated(EnumType.STRING)
    private MenuFeat feature;


    /*
        JPA 연관 관계 매핑
     */

    // store (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    // 데이터 필수 변경이므로 setter를 열었음
    public void setStore(Store store) {
        this.store = store;
    }

    @OneToOne(mappedBy = "menu", cascade = CascadeType.ALL)
    private UploadFile file;

    public void setFile(UploadFile file) {
        this.file = file;
    }

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<HashtagOfMenu> hashtagOfMenuList = new ArrayList<>();



    /*
        생성자
     */
    @Builder
    public Menu(Long id, String name, Integer price, int discountPrice,
                LocalDateTime discountStart, LocalDateTime discountEnd,
                MenuType type, MenuFeat feature, Store store, UploadFile file,
                List<HashtagOfMenu> hashtagOfMenuList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discountStart = discountStart;
        this.discountEnd = discountEnd;
        this.type = type;
        this.feature = feature;
        this.store = store;
        this.file = file;
        this.hashtagOfMenuList = hashtagOfMenuList;
    }

    /*
        === 연관 관계 편의 메소드 ===
     */

    /*
        === 비즈니스 로직 추가 ===
     */
    public void updateWithoutImage(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.discountPrice = menu.getDiscountPrice();
        this.discountStart = menu.getDiscountStart();
        this.discountEnd = menu.getDiscountEnd();
        this.type = menu.getType();
        this.feature = menu.getFeature();
        this.hashtagOfMenuList = menu.getHashtagOfMenuList();
    }
}