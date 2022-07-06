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
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

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

        Store store = new Store(
                "칸다 소바", 123L, 123L, "010-1234-1234",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "안녕하세요", "칸다 소바입니다.", null, StoreStatus.OPEN,
                new Address("부산광역시", "해운대구", "123489")
        );
        em.persist(store);
    }

    @Test
    @DisplayName("매장 등록")
    void saveStore() {
        Store store = new Store(
                "맥도날드", 123L, 123L, "010-1234-1234",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "안녕하세요", "맥도날드입니다.", null, StoreStatus.OPEN,
                new Address("부산광역시", "해운대구", "123489")
        );

        Long storeId = storeService.saveStore(store);
        Store findStore = storeService.findOne(storeId);

        assertThat(findStore.getId()).isEqualTo(storeId);
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

        assertThat(stores.size()).isEqualTo(1);
    }



}