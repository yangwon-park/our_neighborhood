package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.store.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class StoreServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    StoreService storeService;

    // Querydsl 사용
    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @DisplayName("매장 등록, 카테고리 정상 등록 확인")
    void saveStore() {

        List<String> offDays = new ArrayList<>();
        offDays.add("일요일");

        Store store = new Store("칸다 소바", 35.1612928, 129.1600985, "0517311660",
                LocalTime.of(9, 00), LocalTime.of(21, 00), LocalTime.of(15, 30), LocalTime.of(17, 00),
                null, "안녕하세요 칸다 소바입니다.", offDays , StoreStatus.OPEN,
                new Address("부산 해운대구 구남로30번길 8-3", "부산 해운대구 우동 544-15", "48094", "1층"));

        // AddDTO에는 storeId, StoreStatus 없음
        StoreDTO.Add dto = new StoreDTO.Add(store);

        List<Category> categoryList = new ArrayList<>();
        Category category1 = new Category("음식점", 1L, null);
        Category category2 = new Category("일식", 2L, category1);

        categoryList.add(category1);
        categoryList.add(category2);

        Long storeId = storeService.save(dto, categoryList);
        Store findStore = storeService.findOne(storeId);

        // 등록된 매장의 이름이 조회한 매장의 이름과 일치하는가 확인
        assertThat(findStore.getName()).isEqualTo(store.getName());

        // 카테고리 등록 정상 작동 확인 (2개의 카테고리가 들어갔나 확인)
        assertThat(findStore.getCategoryOfStoreList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("해당하는 이름의 매장 찾기")
    void findOneStore() {
        List<Store> findStoreList = storeService.searchByKeyword("칸다");

        // 칸다라는 매장은 한 개 뿐
        assertThat(findStoreList).size().isEqualTo(1);
    }



    @Test
    @DisplayName("모든 매장 찾기")
    void findAllStore() {
        List<Store> stores = storeService.findStores();

        assertThat(stores.size()).isEqualTo(11);
    }

    // 쉬는 날이 있는 가게 등록 한 후 다시 구현
    @Test
    @DisplayName("현재 요일 구하기")
    void today() {
        LocalDate date = LocalDate.now();
        String today = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        LocalTime time = LocalTime.now();

        Store findStore = storeService.searchByKeyword("맥도").get(0);

        List<String> offDays = findStore.getOffDays();

        LocalTime openingTime = findStore.getOpeningTime();
        LocalTime closingTime = findStore.getClosingTime();


        assertThat(openingTime).isBefore(time);
        assertThat(closingTime).isAfter(time);

        assertThat(today).contains(offDays);
    }







}