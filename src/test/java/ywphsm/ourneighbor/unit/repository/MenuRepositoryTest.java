package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MenuRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    StoreRepository storeRepository;

    @BeforeEach
    void beforeEach() {
        Menu menu1 = Menu.builder()
                .name("메뉴 1")
                .price(10000)
                .type(MenuType.MAIN)
                .build();

        Menu menu2 = Menu.builder()
                .name("메뉴 2")
                .price(12000)
                .type(MenuType.MAIN)
                .build();

        Menu menu3 = Menu.builder()
                .name("메뉴 3")
                .price(15000)
                .type(MenuType.MAIN)
                .build();

        Menu menu4 = Menu.builder()
                .name("메뉴 4")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        Menu menu5 = Menu.builder()
                .name("메뉴 5")
                .price(50000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu1);
        tem.persist(menu2);
        tem.persist(menu3);
        tem.persist(menu4);
        tem.persist(menu5);

        Store store1 = Store.builder()
                .name("매장 X")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .menuList(new ArrayList<>())
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Store store2 = Store.builder()
                .name("매장 Y")
                .address(new Address("테스트 로", "22222", "48017", "상세 주소"))
                .lat(35.174816681475)
                .lon(129.196027224944)
                .point(point(WGS84, g(129.196027224944, 35.174816681475)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .menuList(new ArrayList<>())
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        tem.persist(store1);
        tem.persist(store2);

        store1.addMenu(menu1);
        store1.addMenu(menu2);
        store1.addMenu(menu3);
        store2.addMenu(menu4);
        store2.addMenu(menu5);
    }

    @Test
    @DisplayName("메뉴 저장")
    void should_SaveAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴 0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        Menu savedMenu = menuRepository.save(menu0);

        assertThat(savedMenu.getName()).isEqualTo("메뉴 0");
    }

    @Test
    @DisplayName("메뉴 수정")
    void should_UpdateAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴 0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu0);

        Menu updateMenu = Menu.builder()
                .name("업데이트 메뉴")
                .price(25000)
                .type(MenuType.SIDE)
                .build();

        menu0.updateWithoutImage(updateMenu);

        assertThat(menu0.getName()).isEqualTo("업데이트 메뉴");
    }

    @Test
    @DisplayName("메뉴 삭제 후 IsEmpty 확인")
    void should_IsEmpty_When_DeleteAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴 0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu0);

        menuRepository.deleteById(menu0.getId());

        Optional<Menu> findMenu = menuRepository.findById(menu0.getId());
        assertThat(findMenu).isEmpty();
    }

    @Test
    @DisplayName("X 매장에 메뉴 등록 후 해당 매장의 모든 메뉴 조회")
    void should_AddMenusToXStoreAndFindAllMenus_When_InXStore() {
        Menu menu0 = Menu.builder()
                .name("메뉴 0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu0);

        Store findStore = storeRepository.findByName("매장 X").get(0);

        findStore.addMenu(menu0);

        assertThat(findStore.getMenuList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("X 매장에 중복 메뉴 등록시 True 반환")
    void should_IsTrue_When_AddADuplicateMenuToXStore() {
        Store findStore = storeRepository.findByName("매장 X").get(0);

        Menu menu1 = Menu.builder()
                .name("메뉴 1")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu1);

        Boolean checkDuplicateMenu = menuRepository.existsByNameAndStore(menu1.getName(), findStore);

        assertThat(checkDuplicateMenu).isTrue();
    }
}
