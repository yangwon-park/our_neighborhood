package ywphsm.ourneighbor.unit.repository;

import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.distance.Location;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.builder.DSL.ring;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static ywphsm.ourneighbor.domain.store.distance.Direction.*;
import static ywphsm.ourneighbor.domain.store.distance.Direction.SOUTHEAST;
import static ywphsm.ourneighbor.domain.store.distance.Distance.calculatePoint;

@Slf4j
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
        
        /*
            store1 ~ store7까지 범위에 포함되는 매장들
         */
        Store store1 = Store.builder()
                .name("테스트 매장 1")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store2 = Store.builder()
                .name("테스트 매장 2")
                .address(new Address("테스트 로", "22222", "48017", "상세 주소"))
                .lat(35.174816681475)
                .lon(129.196027224944)
                .point(point(WGS84, g(129.196027224944, 35.174816681475)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store3 = Store.builder()
                .name("테스트 매장 3")
                .address(new Address("테스트 로", "33333", "48017", "상세 주소"))
                .lat(35.173114068165)
                .lon(129.175206373038)
                .point(point(WGS84, g(129.175206373038, 35.173114068165)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store4 = Store.builder()
                .name("매장 4")
                .address(new Address("테스트 로", "44444", "48017", "상세 주소"))
                .lat(35.1746804545724)
                .lon(129.196552443575)
                .point(point(WGS84, g(129.196552443575, 35.1746804545724)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store5 = Store.builder()
                .name("매장 5")
                .address(new Address("테스트 로", "55555", "48017", "상세 주소"))
                .lat(35.176595357839)
                .lon(129.169832029527)
                .point(point(WGS84, g(129.169832029527, 35.176595357839)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store6 = Store.builder()
                .name("매장 6")
                .address(new Address("테스트 로", "66666", "48017", "상세 주소"))
                .lat(35.1712314290717)
                .lon(129.172098256716)
                .point(point(WGS84, g(129.172098256716, 35.1712314290717)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store7 = Store.builder()
                .name("매장 7")
                .address(new Address("테스트 로", "77777", "48017", "상세 주소"))
                .lat(35.1637214604028)
                .lon(129.162621202408)
                .point(point(WGS84, g(129.162621202408, 35.1637214604028)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store8 = Store.builder()
                .name("매장 8")
                .address(new Address("테스트 로", "88888", "48017", "상세 주소"))
                .lat(33.48)
                .lon(123.48)
                .point(point(WGS84, g(123.48, 33.48)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store9 = Store.builder()
                .name("매장 9")
                .address(new Address("테스트 로", "99999", "48017", "상세 주소"))
                .lat(33.49)
                .lon(123.49)
                .point(point(WGS84, g(123.49, 33.49)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store store10 = Store.builder()
                .name("매장 10")
                .address(new Address("테스트 로", "12121", "48017", "상세 주소"))
                .lat(33.50)
                .lon(123.50)
                .point(point(WGS84, g(123.50, 33.50)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        UploadFile uploadFile1 = new UploadFile("업로드명1", "저장명1", "URL1");
        UploadFile uploadFile2 = new UploadFile("업로드명2", "저장명2", "URL2");
        UploadFile uploadFile3 = new UploadFile("업로드명3", "저장명3", "URL3");
        UploadFile uploadFile4 = new UploadFile("업로드명4", "저장명4", "URL4");
        UploadFile uploadFile5 = new UploadFile("업로드명5", "저장명5", "URL5");
        UploadFile uploadFile6 = new UploadFile("업로드명6", "저장명6", "URL6");
        UploadFile uploadFile7 = new UploadFile("업로드명7", "저장명7", "URL7");
        UploadFile uploadFile8 = new UploadFile("업로드명8", "저장명8", "URL8");
        UploadFile uploadFile9 = new UploadFile("업로드명9", "저장명9", "URL9");
        UploadFile uploadFile10 = new UploadFile("업로드명10", "저장명10", "URL10");

        tem.persist(store1);
        tem.persist(store2);
        tem.persist(store3);
        tem.persist(store4);
        tem.persist(store5);
        tem.persist(store6);
        tem.persist(store7);
        tem.persist(store8);
        tem.persist(store9);
        tem.persist(store10);

        uploadFile1.addStore(store1);
        uploadFile2.addStore(store2);
        uploadFile3.addStore(store3);
        uploadFile4.addStore(store4);
        uploadFile5.addStore(store5);
        uploadFile6.addStore(store6);
        uploadFile7.addStore(store7);
        uploadFile8.addStore(store8);
        uploadFile9.addStore(store9);
        uploadFile10.addStore(store10);
    }

    @Test
    @DisplayName("매장 저장")
    void should_SaveAStore() {
        Store store = Store.builder()
                .name("테스트 매장 n")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(33.45)
                .lon(123.45)
                .point(point(WGS84, g(123.45, 33.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        Store savedStore = storeRepository.save(store);

        assertThat(savedStore.getName()).isEqualTo("테스트 매장 n");
    }

    @Test
    @DisplayName("매장 수정")
    void should_UpdateAStore() {
        Store store = Store.builder()
                .name("테스트 매장 n")
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
                .point(point(WGS84, g(123.45, 35.45)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .build();

        store.update(updateStore);

        assertThat(store.getName()).isEqualTo("업데이트된 매장 이름");
    }

    @Test
    @DisplayName("매장 삭제")
    void should_IsEmpty_When_DeleteStore() {
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
    void should_FindAllStores() {
        List<Store> storeList = storeRepository.findAllStores();

        assertThat(storeList.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("키워드로 매장 조회")
    void should_FindStores_When_ContainsKeyword() {
        List<Store> storeList = storeRepository.searchByKeyword("테스트");

        assertThat(storeList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("범위 내에 존재하는 매장의 개수 반환")
    void should_ReturnCount_When_InPolygon() {
        Long cnt = storeRepository.countStoreInPolygon(getPolygon());

        assertThat(cnt).isEqualTo(7);
    }

    @Test
    @DisplayName("범위 내 랜덤으로 7개 조회")
    void should_FindStores_When_InPolygonAndTop7() {
        PageRequest pageRequest = PageRequest.of(0, 7);
        List<SimpleSearchStoreDTO> storeList = storeRepository.searchTop7Random(getPolygon(), pageRequest);

        assertThat(storeList.size()).isEqualTo(7);
    }

    private static Polygon<G2D> getPolygon() {
        double dist = 3;
        double toCorner = dist * (Math.sqrt(2));

        double lat = 35.1633408;
        double lon = 129.1845632;

        Location northEast = calculatePoint(lat, lon, toCorner, NORTHEAST.getAngle());
        Location northWest = calculatePoint(lat, lon, toCorner, NORTHWEST.getAngle());
        Location southWest = calculatePoint(lat, lon, toCorner, SOUTHWEST.getAngle());
        Location southEast = calculatePoint(lat, lon, toCorner, SOUTHEAST.getAngle());

        double nex = northEast.getLon();
        double ney = northEast.getLat();

        double nwx = northWest.getLon();
        double nwy = northWest.getLat();

        double swx = southWest.getLon();
        double swy = southWest.getLat();

        double sex = southEast.getLon();
        double sey = southEast.getLat();

        return polygon(WGS84, ring(g(nex, ney),
                g(nwx, nwy), g(swx, swy), g(sex, sey), g(nex, ney)));
    }
}
