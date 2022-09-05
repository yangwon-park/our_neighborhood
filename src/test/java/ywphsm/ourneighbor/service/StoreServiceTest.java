package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Address;
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
import static ywphsm.ourneighbor.domain.store.QStore.*;

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
        List<String> offDays = new ArrayList<>();
        offDays.add("금요일");
        Store store = new Store("쿠다", 35.1612928, 129.1600985, "0517311660",
                LocalTime.of(9, 00), LocalTime.of(20, 00), LocalTime.of(15, 30), LocalTime.of(17, 00),
                null, "안녕하세요 칸다 소바입니다.", offDays , StoreStatus.OPEN, new Address("부산광역시 해운대구 구남로 30번길 8-3 1층", "48094", "1234", null));
        em.persist(store);
    }

    @Test
    @DisplayName("매장 등록")
    void saveStore() {

        List<String> offDays = new ArrayList<>();
        offDays.add("일요일");

        Store store = new Store("칸다 소바", 35.1612928, 129.1600985, "0517311660",
                LocalTime.of(9, 00), LocalTime.of(21, 00), LocalTime.of(15, 30), LocalTime.of(17, 00),
                null, "안녕하세요 칸다 소바입니다.", offDays , StoreStatus.OPEN,
                new Address("부산 해운대구 구남로30번길 8-3", "부산 해운대구 우동 544-15", "48094", "1층"));

        // AddDTO에는 storeId, StoreStatus 없음
        StoreDTO.Add dto = new StoreDTO.Add(store);

        Category category = new Category("일식", 1L, null);

        Long storeId = storeService.saveStore(dto, category);
        Store findStore = storeService.findOne(storeId);

        assertThat(findStore.getName()).isEqualTo(store.getName());
        
        // 카테고리 일치 여부 확인
        assertThat(findStore.getCategoryOfStoreList().get(0).getCategory().getName()).isEqualTo("일식");
    }

    @Test
    @DisplayName("해당하는 이름의 매장 찾기")
    void findOneStore() {
        Store findStore = queryFactory
                .select(store)
                .from(store)
                .where(store.name.eq("칸다 소바"))
                .fetchOne();


        if (findStore != null) {
            assertThat(findStore.getName()).isEqualTo("칸다 소바");
        }
    }



    @Test
    @DisplayName("모든 매장 찾기")
    void findAllStore() {
        List<Store> stores = storeService.findStores();

        assertThat(stores.size()).isEqualTo(12);
    }

    @Test
    @DisplayName("검색어가 매장명에 포함되는 매장 찾기")
    void searchStoreByName() {
        String cond = "소바";
        List<Store> stores = storeService.searchByKeyword(cond);

        assertThat(stores.size()).isEqualTo(1);

        for (Store store1 : stores) {
            System.out.println("store1 = " + store1.getName());
        }
    }

    @Test
    @DisplayName("현재 요일 구하기")
    void today() {
        LocalDate date = LocalDate.now();
        String today = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        LocalTime time = LocalTime.now();


        Store findStore = storeService.searchByKeyword("쿠다").get(0);

        List<String> offDays = findStore.getOffDays();

        LocalTime openingTime = findStore.getOpeningTime();
        LocalTime closingTime = findStore.getClosingTime();


        assertThat(openingTime).isBefore(time);
        assertThat(closingTime).isAfter(time);

        assertThat(today).contains(offDays);
    }







}