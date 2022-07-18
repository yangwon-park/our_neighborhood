package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.CategoryOfStore;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Test
    @DisplayName("카테고리 추가")
    void saveCategory() {

        Store store = new Store(
                "칸다 소바", 123L, 123L, "010-1234-1234",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "안녕하세요", "칸다 소바입니다.", null, StoreStatus.OPEN,
                new Address("부산광역시", "해운대구", "123489")
        );

        CategoryOfStore categoryOfStore = new CategoryOfStore(store);
        Category korean_food = new Category("한식", "categoryOfStore", null, null);

    }
}