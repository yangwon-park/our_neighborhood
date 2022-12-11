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
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .name("메뉴1")
                .price(10000)
                .type(MenuType.MAIN)
                .build();

        Menu menu2 = Menu.builder()
                .name("메뉴2")
                .price(12000)
                .type(MenuType.SIDE)
                .build();

        Menu menu3 = Menu.builder()
                .name("메뉴3")
                .price(15000)
                .type(MenuType.DESSERT)
                .build();

        Menu menu4 = Menu.builder()
                .name("메뉴4")
                .price(20000)
                .type(MenuType.BEVERAGE)
                .build();

        Menu menu5 = Menu.builder()
                .name("메뉴5")
                .price(50000)
                .type(MenuType.DRINK)
                .build();

        Menu menu6 = Menu.builder()
                .name("메뉴6")
                .price(0)
                .type(MenuType.MENU)
                .build();

        Menu menu7 = Menu.builder()
                .name("메뉴7")
                .price(10000)
                .type(MenuType.MAIN)
                .build();


        Menu menu8 = Menu.builder()
                .name("메뉴8")
                .price(20000)
                .type(MenuType.DESSERT)
                .build();

        /*
            Menu UploadFile 간 연관 관계 주인이 UploadFile => 별도의 Persist를 해주지 않아도
            addMenu에서 persist가 됨
         */
        UploadFile uploadFile1 = new UploadFile("업로드명1", "저장명1", "URL1");
        UploadFile uploadFile2 = new UploadFile("업로드명2", "저장명2", "URL2");
        UploadFile uploadFile3 = new UploadFile("업로드명3", "저장명3", "URL3");
        UploadFile uploadFile4 = new UploadFile("업로드명4", "저장명4", "URL4");
        UploadFile uploadFile5 = new UploadFile("업로드명5", "저장명5", "URL5");
        UploadFile uploadFile6 = new UploadFile("업로드명6", "저장명6", "URL6");
        UploadFile uploadFile7 = new UploadFile("업로드명7", "저장명7", "URL7");
        UploadFile uploadFile8 = new UploadFile("업로드명8", "저장명8", "URL8");

        uploadFile1.addMenu(menu1);
        uploadFile2.addMenu(menu2);
        uploadFile3.addMenu(menu3);
        uploadFile4.addMenu(menu4);
        uploadFile5.addMenu(menu5);
        uploadFile6.addMenu(menu6);
        uploadFile7.addMenu(menu7);
        uploadFile8.addMenu(menu8);

        Store store1 = Store.builder()
                .name("매장X")
                .address(new Address("테스트로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .menuList(new ArrayList<>())
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Store store2 = Store.builder()
                .name("매장Y")
                .address(new Address("테스트로", "22222", "48017", "상세 주소"))
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
        store1.addMenu(menu4);
        store1.addMenu(menu5);
        store1.addMenu(menu6);
        store2.addMenu(menu7);
        store2.addMenu(menu8);
    }

    @Test
    @DisplayName("메뉴 저장")
    void should_SaveAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        Menu savedMenu = menuRepository.save(menu0);

        assertThat(savedMenu.getName()).isEqualTo("메뉴0");
    }

    @Test
    @DisplayName("메뉴 수정")
    void should_UpdateAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴0")
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

        assertThat(menu0.getName()).isEqualTo(updateMenu.getName());
    }

    @Test
    @DisplayName("메뉴 삭제 후 IsEmpty 확인")
    void should_IsEmpty_When_DeleteAMenu() {
        Menu menu0 = Menu.builder()
                .name("메뉴0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu0);

        menuRepository.deleteById(menu0.getId());

        Optional<Menu> findMenu = menuRepository.findById(menu0.getId());
        assertThat(findMenu).isEmpty();
    }

    @Test
    @DisplayName("매장X에 메뉴 등록 후 해당 매장의 모든 메뉴 조회")
    void should_AddMenusToXStoreAndFindAllMenus_When_InXStore() {
        Menu menu0 = Menu.builder()
                .name("메뉴0")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu0);

        Store findStore = storeRepository.findByName("매장X").get(0);

        findStore.addMenu(menu0);

        assertThat(findStore.getMenuList().size()).isEqualTo(7);
    }


    @Test
    @DisplayName("메뉴1 조회 후, 등록된 매장이 매장X가 맞는지 확인")
    void should_FindMenu1_When_InXStore() {
        String name = "메뉴1";
        String targetStore = "매장X";

        Menu findMenu = menuRepository.findByName(name).get(0);

        tem.persist(findMenu);

        assertThat(findMenu.getStore().getName()).isEqualTo(targetStore);
    }

    @Test
    @DisplayName("매장X에 중복 메뉴 등록시 True 반환")
    void should_IsTrue_When_AddADuplicateMenuToXStore() {
        Store findStore = storeRepository.findByName("매장X").get(0);

        Menu menu1 = Menu.builder()
                .name("메뉴1")
                .price(20000)
                .type(MenuType.MAIN)
                .build();

        tem.persist(menu1);

        Boolean checkDuplicateMenu = menuRepository.existsByNameAndStore(menu1.getName(), findStore);

        assertThat(checkDuplicateMenu).isTrue();
    }

    @Test
    @DisplayName("매장X에 등록된 메뉴들이 메뉴 타입 정렬 기준에 알맞게 조회되는지 확인 (메뉴판은 제외)")
    void should_FindMenusInXStore_When_CaseByOrderByType() {
        /*
            정렬 기준
                MAIN, SIDE, DESSERT, BEVERAGE, DRINK
         */
        Long xId = storeRepository.findByName("매장X").get(0).getId();
        List<Menu> result = menuRepository.findByStoreIdWithoutMenuTypeIsMenuCaseByOrderByType(xId);
        List<String> menuNameList = result.stream().map(Menu::getName).collect(Collectors.toList());

        assertThat(menuNameList).hasSize(5).containsExactly("메뉴1", "메뉴2", "메뉴3", "메뉴4", "메뉴5");
    }

    @Test
    @DisplayName("매장X의 메뉴판 이미지의 URL 조회")
    void should_FindMenuImageURL_When_InXStoreAndMenuTypeIsMenu() {
        Long xId = storeRepository.findByName("매장X").get(0).getId();
        List<String> menuNameList = menuRepository.findImageByMenuTypeIsMenu(xId);

        assertThat(menuNameList).contains("URL6");
    }

}
