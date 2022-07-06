package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
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

    private String name;
//
//    @ManyToOne
//    private List<Store> stores = new ArrayList<>();

    /*
        계층형 구조 => 셀프로 양방향 연관 관계를 걸어줌
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}
