package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.QMenu;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static ywphsm.ourneighbor.domain.QMenu.*;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    MenuService menuService;

    @Autowired
    StoreService storeService;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Store store1 = new Store("칸다 소바", 35.1612928, 129.1600985, "0517311660",
                LocalTime.of(9, 00, 00), LocalTime.of(20, 00, 00), LocalTime.of(15, 30, 00), LocalTime.of(17, 00, 00),
                null, "안녕하세요 칸다 소바입니다.", null, StoreStatus.OPEN, null);

        Store store2 = new Store("맥도날드", 35.1600985, 129.1596415, "07072091629",
                LocalTime.of(00, 00, 00), LocalTime.of(00, 00, 00), null, null,
                null, "맥도날드로 오세요.", null, StoreStatus.OPEN, null);

        em.persist(store1);
        em.persist(store2);

        Menu menu1 = new Menu(
                "마제 소바", 10000, 0,
                null, null, store1
        );
        Menu menu2 = new Menu(
                "아우라 소바", 12000, 0,
                null, null, store1
        );
        Menu menu3 = new Menu(
                "메밀 소바", 8000, 0,
                null, null, store1
        );
        Menu menu4 = new Menu(
                "햄버거", 7000, 0,
                null, null, store2
        );
        Menu menu5 = new Menu(
                "감자 튀김", 2000, 0,
                null, null, store2
        );
        em.persist(menu1);
        em.persist(menu2);
        em.persist(menu3);
        em.persist(menu4);
        em.persist(menu5);
    }

//    @Test
//    @DisplayName("메뉴 등록")
//    void saveMenu() {
//        List<Store> storeList = storeService.findByName("칸다 소바");
//        Store store = storeList.get(0);
//
//        Menu menu = new Menu(
//                "마제 소바", 10000, 0,
//                null, null, store
//        );
//
//        Long menuId = menuService.saveMenu(menu);
//        System.out.println(store);
//        assertThat(menu).isEqualTo(menuService.findOne(menuId));
//    }


    @Test
    @DisplayName("한 매장의 모든 메뉴 불러오기")
    void findAllMenuInAStore() {
        List<Store> stores = storeService.findStores();

        for (Store store : stores) {
            List<Menu> menuList = findMenuList(store);

            for (Menu menu : menuList) {
                if (menu.getStore().getId().equals(store.getId())) {
                    assertThat(store.getName()).isEqualTo(menu.getStore().getName());
                    System.out.println("메뉴 이름 : " + menu.getName());
                }
            }
        }
    }

    // 한 매장 내의 메뉴 불러오는 메소드
    private List<Menu> findMenuList(Store store) {
        return queryFactory
                .select(menu)
                .from(menu)
                .where(menu.store.id.eq(store.getId()))
                .fetch();
    }
}