package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryOfStore {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;


    // 연관 관계 편의 메소드를 위해 setter 열어둠
    public void setCategory(Category category) {
        this.category = category;

    }

    public void setStore(Store store) {
        this.store = store;
    }

    /*
        === 연관 관계 편의 메소드 ===
    */
    public CategoryOfStore addCategory(Category category, Store store) {
        this.setCategory(category);
        this.setStore(store);
        category.getCategoryOfStoreList().add(this);
        store.getCategoryOfStoreList().add(this);

        return this;
    }


    /*
        생성자
     */

    public CategoryOfStore(Category category) {
        this.category = category;
    }

    public CategoryOfStore(Category category, Store store) {
        this.category = category;
        this.store = store;
    }
}
