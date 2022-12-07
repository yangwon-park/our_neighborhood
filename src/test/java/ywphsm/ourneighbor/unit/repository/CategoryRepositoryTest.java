package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static ywphsm.ourneighbor.domain.category.CategoryOfStore.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    StoreRepository storeRepository;

    @BeforeEach
    void beforeEach() {

        Category restaurant = Category.builder()
                .name("동네 맛집")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category cafe = Category.builder()
                .name("카페 / 베이커리")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category bar = Category.builder()
                .name("인기 술집")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category leisure = Category.builder()
                .name("문화 / 여가")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category1 = Category.builder()
                .name("카테고리1")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category2 = Category.builder()
                .name("카테고리2")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category3 = Category.builder()
                .name("카테고리3")
                .depth(2L)
                .parent(category1)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category4 = Category.builder()
                .name("카테고리4")
                .depth(2L)
                .parent(category2)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category5 = Category.builder()
                .name("카테고리5")
                .parent(category3)
                .depth(3L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category category6 = Category.builder()
                .name("카테고리6")
                .parent(category2)
                .depth(3L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        tem.persist(restaurant);
        tem.persist(cafe);
        tem.persist(bar);
        tem.persist(leisure);
        tem.persist(category1);
        tem.persist(category2);
        tem.persist(category3);
        tem.persist(category4);
        tem.persist(category5);
        tem.persist(category6);

        Store storeX = Store.builder()
                .name("매장X")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Store storeY = Store.builder()
                .name("매장Y")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .build();

        tem.persist(storeX);
        tem.persist(storeY);

        linkCategoryAndStore(category1, storeX);
        linkCategoryAndStore(category3, storeX);
        linkCategoryAndStore(category5, storeX);
        linkCategoryAndStore(category2, storeY);
        linkCategoryAndStore(category4, storeY);
        linkCategoryAndStore(category6, storeY);
    }

    @Test
    @DisplayName("카테고리 명, 카테고리 뎁스로 조회 성공")
    void should_FindACategory_When_ByCategoryNameAndDepth() {
        Category findCategory = categoryRepository.findByNameAndDepth("카테고리1", 1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 뎁스의 카테고리를 찾지 못했습니다."));

        assertThat(findCategory.getName()).isEqualTo("카테고리1");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 명, 카테고리 뎁스로 조회시 예외 발생")
    void should_IllegalArgumentException_When_FindCategoryByCategoryNameAndDepth() {
        assertThatThrownBy(
                () -> categoryRepository.findByNameAndDepth("없는 카테고리", 23L)
                        .orElseThrow(() -> new IllegalArgumentException("해당 뎁스의 카테고리를 찾지 못했습니다.")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("부모 카테고리가 없는 카테고리 조회")
    void should_FindCategories_When_ByParentIsNull() {
        /*
            Depth가 1L인 카테고리는 부모 카테고리가 없음
         */
        List<Category> categoryList = categoryRepository.findByParentIsNull();
        List<String> result = categoryList.stream()
                .map(Category::getName).collect(Collectors.toList());

        assertThat(result).containsExactly("동네 맛집", "카페 / 베이커리", "인기 술집", "문화 / 여가", "카테고리1", "카테고리2");
    }

    @Test
    @DisplayName("카테고리를 정렬 조건에 맞게 조회")
    void should_FindAllCategories_When_ByOrderByDepthAscParentIdAscNameAsc() {
        List<Category> categoryList = categoryRepository.findAllByOrderByDepthAscParentIdAscNameAsc();
        List<String> result = categoryList.stream()
                .map(Category::getName).collect(Collectors.toList());

        assertThat(result).containsExactly("동네 맛집", "문화 / 여가", "인기 술집", "카테고리1", "카테고리2", "카페 / 베이커리", "카테고리3", "카테고리4", "카테고리6", "카테고리5");
    }

    @Test
    @DisplayName("카테고리 명, 카테고리 뎁스가 같은 카테고리 등록 시, 중복으로 인한 true 반환")
    void should_IsTrue_When_AddACategoryByNameEqualsAndByDepthEquals() {
        Boolean check = categoryRepository.existsByNameAndDepth("카테고리1", 1L);

        assertThat(check).isTrue();
    }

    @Test
    @DisplayName("카테고리 명은 같지만 카테고리 뎁스가 다른 카테고리 등록 시, 중복이 아니므로 false 반환")
    void should_IsFalse_When_AddACategoryByNameEqualsAndDepthNot() {
        Boolean check = categoryRepository.existsByNameAndDepth("카테고리1", 2L);

        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("카테고리 명, 부모 카테고리가 같은 카테고리 등록 시, 중복으로 인한 true 반환")
    void should_IsTrue_When_AddACategoryByNameEqualsAndParentEquals() {
        Category category = categoryRepository.findByName("카테고리1");
        Boolean check = categoryRepository.existsByNameAndParent("카테고리3", category);

        assertThat(check).isTrue();
    }


    @Test
    @DisplayName("카테고리 명은 같지만 부모 카테고리가 다른 카테고리 등록 시, 중복이 아니므로 false 반환")
    void should_IsFalse_When_AddACategoryByNameEqualsAndParentNot() {
        Category category = categoryRepository.findByName("카테고리2");
        Boolean check = categoryRepository.existsByNameAndParent("카테고리3", category);

        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("뎁스로 조회한 카테고리가 정렬 기준에 알맞게 조회되는지 확인 (Limit - 4)")
    void should_FindCategories_When_ByDepthCaseByOrderByNameTop4() {
        /*
            정렬 기준
                동네 맛집, 카페 / 베이커리, 인기 술집, 문화 / 여가, 나머지...
         */
        Long depth = 1L;
        List<Category> categoryList = categoryRepository.findByDepthCaseByOrderByName(1L);

        List<String> result = categoryList.stream()
                .map(Category::getName).collect(Collectors.toList());

        assertThat(result).containsExactly("동네 맛집", "카페 / 베이커리", "인기 술집", "문화 / 여가");
    }

    @Test
    @DisplayName("매장Z에 카테고리 등록 후, 정상 등록 확인")
    void should_AddToStoreZWithCategoriesAndFindCategories_WhenInStoreZ() {
        Store storeZ = Store.builder()
                .name("매장Z")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .build();

        tem.persist(storeZ);

        Category category = categoryRepository.findByNameAndDepth("카테고리1", 1L)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 없습니다."));

        linkCategoryAndStore(category, storeZ);

        List<CategoryOfStore> categoryOfStoreList = storeZ.getCategoryOfStoreList();
        List<Category> result = categoryOfStoreList.stream()
                .map(CategoryOfStore::getCategory).collect(Collectors.toList());

        assertThat(result).containsExactly(category);
    }

    @Test
    @DisplayName("매장X에 등록돼있는 카테고리 확인")
    void should_FineCategories_When_InStoreX() {
        Store storeX = storeRepository.findByName("매장X").get(0);
        List<CategoryOfStore> categoryOfStoreList = storeX.getCategoryOfStoreList();

        List<String> result = categoryOfStoreList.stream()
                .map(cs -> cs.getCategory().getName()).collect(Collectors.toList());

        assertThat(result).containsExactly("카테고리1", "카테고리3", "카테고리5");
    }

//    @Test
//    @DisplayName("매장X에 등록돼있는 카테고리 등록 해제")
//    void should_DeleteCategories_When_InStoreX() {
//        Store storeX = storeRepository.findByName("매장X").get(0);
//        List<CategoryOfStore> beforeList = storeX.getCategoryOfStoreList();
//
//        List<Category> categoryList = beforeList.stream()
//                .map(CategoryOfStore::getCategory).collect(Collectors.toList());
//
//        for (Category category : categoryList) {
//            categoryRepository.deleteByCategoryLinkedCategoryOfStore(category);
//            System.out.println("category.getId() = " + category.getId());
//            System.out.println("category.getId() = " + category.getName());
//        }
//
//        List<CategoryOfStore> afterList = storeX.getCategoryOfStoreList();
//
//        for (CategoryOfStore categoryOfStore : afterList) {
//            System.out.println("categoryOfStore = " + categoryOfStore.getId());
//            System.out.println("categoryOfStore = " + categoryOfStore.getCategory().getName());
//            System.out.println("categoryOfStore = " + categoryOfStore.getStore().getName());
//        }
//    }
}
