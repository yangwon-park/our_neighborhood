package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.hashtag.hashtagofmenu.HashtagOfMenuRepository;
import ywphsm.ourneighbor.repository.hashtag.hashtagofstore.HashtagOfStoreRepository;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu.*;
import static ywphsm.ourneighbor.domain.hashtag.HashtagOfStore.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HashtagRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    HashtagOfMenuRepository hashtagOfMenuRepository;

    @Autowired
    HashtagOfStoreRepository hashtagOfStoreRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    StoreRepository storeRepository;

    @BeforeEach
    void beforeEach() {
        Hashtag hashtag1 = Hashtag.builder()
                .name("해쉬태그1")
                .hashtagOfMenuList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Hashtag hashtag2 = Hashtag.builder()
                .name("해쉬태그2")
                .hashtagOfMenuList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Hashtag hashtag3 = Hashtag.builder()
                .name("해쉬태그3")
                .hashtagOfMenuList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        tem.persist(hashtag1);
        tem.persist(hashtag2);
        tem.persist(hashtag3);

        Menu menu1 = Menu.builder()
                .name("메뉴1")
                .price(10000)
                .type(MenuType.MAIN)
                .hashtagOfMenuList(new ArrayList<>())
                .build();

        Menu menu2 = Menu.builder()
                .name("메뉴2")
                .price(12000)
                .type(MenuType.MAIN)
                .hashtagOfMenuList(new ArrayList<>())
                .build();

        tem.persist(menu1);
        tem.persist(menu2);

        linkHashtagAndMenu(hashtag1, menu1);
        linkHashtagAndMenu(hashtag2, menu1);
        linkHashtagAndMenu(hashtag1, menu2);
        linkHashtagAndMenu(hashtag3, menu2);

        Store storeX = Store.builder()
                .name("매장X")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        tem.persist(storeX);

        linkHashtagAndStore(hashtag1, storeX);
        linkHashtagAndStore(hashtag2, storeX);
        linkHashtagAndStore(hashtag2, storeX);
        linkHashtagAndStore(hashtag2, storeX);
        linkHashtagAndStore(hashtag3, storeX);
    }

    @Test
    @DisplayName("해쉬태그 등록")
    void should_SaveAHashtag() {
        Hashtag hashtag = Hashtag.builder()
                .name("해쉬태그")
                .hashtagOfMenuList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Hashtag savedHashtag = hashtagRepository.save(hashtag);

        assertThat(savedHashtag.getName()).isEqualTo(hashtag.getName());
    }

    @Test
    @DisplayName("존재하지 않는 해쉬태그 이름으로 조회시 예외 발생")
    void should_IllegalArgumentException_When_FindHashtagByName() {
        assertThatThrownBy(
                () -> hashtagRepository.findByName("없는 해쉬태그")
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다.")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하는 해쉬태그 이름이면 true 반환")
    void should_IsTrue_When_AddHashtagByNameEquals() {
        boolean check = hashtagRepository.existsByName("해쉬태그1");

        assertThat(check).isTrue();
    }

    @Test
    @DisplayName("메뉴1에 등록된 해쉬태그 조회")
    void should_FindAllHashtags_When_ByInMenu1() {
        Long menuId = menuRepository.findByName("메뉴1").get(0).getId();

        List<HashtagOfMenu> allHashtagByMenuId = hashtagOfMenuRepository.findAllHashtagByMenuId(menuId);

        List<String> result = allHashtagByMenuId.stream()
                .map(hashtagOfMenu -> hashtagOfMenu.getHashtag().getName()).collect(Collectors.toList());

        assertThat(result).hasSize(2).containsExactly("해쉬태그1", "해쉬태그2");
    }
    
    @Test
    @DisplayName("메뉴1에 중복된 해쉬태그가 등록될 경우 true 반환")
    void should_IsTrue_When_AddHashtagByNameEqualsInMenu1() {
        Menu menu = menuRepository.findByName("메뉴1").get(0);

        Hashtag hashtag = hashtagRepository.findByName("해쉬태그1")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다."));

        Boolean check = hashtagOfMenuRepository.existsByHashtagAndMenu(hashtag, menu);

        assertThat(check).isTrue();
    }

    @Test
    @DisplayName("기존에 있는 해쉬태그이지만, 메뉴1에 등록되지 않는 해쉬태그를 등록할 경우, 중복이 아니므로 false 반환")
    void should_IsFalse_When_AddHashtagByNameNotInMenu1() {
        Menu menu = menuRepository.findByName("메뉴1").get(0);

        Hashtag hashtag = hashtagRepository.findByName("해쉬태그3")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다."));

        Boolean check = hashtagOfMenuRepository.existsByHashtagAndMenu(hashtag, menu);

        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("매장에 등록된 해쉬태그를 등록 횟수와 함께 조회")
    void should_FindHashtagsAndCount_When_InStoreX() {
        Long storeId = storeRepository.findByName("매장X").get(0).getId();

        List<HashtagOfStoreDTO.WithCount> dtoList = hashtagOfStoreRepository.findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(storeId);

        List<String> result = dtoList.stream()
                .map(HashtagOfStoreDTO.WithCount::getHashtagName).collect(Collectors.toList());

        assertThat(result).hasSize(3).containsExactly("해쉬태그2", "해쉬태그1", "해쉬태그3");
    }
}
