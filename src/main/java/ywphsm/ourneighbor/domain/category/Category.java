package ywphsm.ourneighbor.domain.category;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString(of = {"id", "name", "depth"})
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
    public Category(Long id, String name, Long depth, List<CategoryOfStore> categoryOfStoreList, Category parent, List<Category> children) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.categoryOfStoreList = categoryOfStoreList;
        this.parent = parent;
        this.children = children;
    }

    public Category(String name, Long depth, Category parent) {
        this.name = name;
        this.depth = depth;
        this.parent = parent;
    }

    /*
        === 연관 관계 편의 메소드 ===
    */

}