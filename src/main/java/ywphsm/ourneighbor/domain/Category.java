package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @NotBlank
    private String name;


    /*
        JPA 연관 관계 매핑
     */
    /*
        Store(다) : Category(다)
            ==> Store(1) : CategoryOfStore(다) : Category (1)
     */
    @OneToMany(mappedBy = "category")
    private List<CategoryOfStore> categoryOfStoreList = new ArrayList<>();


    /*
        계층형 구조 => 셀프로 양방향 연관 관계를 걸어줌
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    
    /*
        생성자
     */


    /*
        === 연관 관계 편의 메소드 ===
    */
    public void addCategory(CategoryOfStore categoryOfStore) {
        categoryOfStoreList.add(categoryOfStore);
        categoryOfStore.setCategory(this);
    }

    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }


}
