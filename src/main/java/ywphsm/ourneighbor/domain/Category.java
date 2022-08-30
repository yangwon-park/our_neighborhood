package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    private Long depth;

    /*
        JPA 연관 관계
     */

    /*
        계층형 구조 => 셀프로 양방향 연관 관계를 걸어줌
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    public void addParentCategory(Category parent) {
        this.parent = parent;
    }

    public void addParentCategoryAndDepth(Category parent, Long depth) {
        this.parent = parent;
        this.depth = depth;
    }

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    // store (N:N)
    @OneToMany(mappedBy = "category")
    private List<CategoryOfStore> categoryOfStoreList = new ArrayList<>();


    /*
        생성자
     */
    @Builder
    public Category(String name, Long depth, List<CategoryOfStore> categoryOfStoreList, Category parent, List<Category> children) {

        this.name = name;
        this.depth = depth;
        this.categoryOfStoreList = categoryOfStoreList;
        this.parent = parent;
        this.children = children;
    }



    /*
        === 연관 관계 편의 메소드 ===
    */
    public void addCategory(CategoryOfStore categoryOfStore) {
        categoryOfStoreList.add(categoryOfStore);
        categoryOfStore.setCategory(this);
    }

    public void addChildCategory(Category child) {
        this.children.add(child);
        child.addParentCategory(this);
    }




}
