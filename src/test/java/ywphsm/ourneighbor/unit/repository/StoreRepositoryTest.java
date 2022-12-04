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
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    StoreRepository storeRepository;


    @BeforeEach
    void beforeEach() {
        Store store1 = Store.builder()
                .name("테스트 매장 1")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();
        Store store2 = Store.builder()
                .name("테스트 매장 2")
                .address(new Address("테스트 로", "22222", "48017", "상세 주소"))
                .lat(33.46)
                .lon(123.46)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();
        Store store3 = Store.builder()
                .name("테스트 매장 3")
                .address(new Address("테스트 로", "33333", "48017", "상세 주소"))
                .lat(33.47)
                .lon(123.47)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        tem.persist(store1);
        tem.persist(store2);
        tem.persist(store3);
    }

    @Test
    @DisplayName("매장 저장")
    void should_save_a_store() {
        Store store = Store.builder()
                .name("테스트 매장 4")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store savedStore = storeRepository.save(store);

        assertThat(savedStore.getName()).isEqualTo("테스트 매장 4");
    }

    @Test
    @DisplayName("매장 수정")
    void should_update_a_store() {
        Store store = Store.builder()
                .name("테스트 매장 4")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        tem.persist(store);

        Store updateStore = Store.builder()
                .name("업데이트된 매장 이름")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        store.update(updateStore);

        assertThat(store.getName()).isEqualTo("업데이트된 매장 이름");
        assertThat(store.getLat()).isEqualTo(35.45);
        assertThat(store.getLon()).isEqualTo(123.45);
    }

    @Test
    @DisplayName("매장 삭제")
    void should_delete_a_store() {
        Store store = Store.builder()
                .name("테스트 매장 4")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        tem.persist(store);

        storeRepository.deleteById(store.getId());

        Optional<Store> findStore = storeRepository.findById(store.getId());
        assertThat(findStore).isEmpty();
    }

    @Test
    @DisplayName("모든 매장 조회")
    void should_find_all_stores() {
        List<Store> storeList = storeRepository.findAllStores();

        assertThat(storeList.size()).isEqualTo(3);
    }
}
