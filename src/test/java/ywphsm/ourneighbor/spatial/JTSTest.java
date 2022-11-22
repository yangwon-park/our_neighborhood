package ywphsm.ourneighbor.spatial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JTSTest {

    @Autowired
    StoreService storeService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void before() {

    }

    @Test
    @DisplayName("단순 조회 후 거리 필터")
    void searchByCategory() {

        String myLat = "35.1633408";
        String myLon = "129.1845632";
        double dist = 3000;

        Long range = 10000000L;

        List<Store> allStores = storeService.findAllStores(range);

//        List<SimpleSearchStoreDTO> dto = allStores.stream().map(SimpleSearchStoreDTO::new).collect(Collectors.toList());
//
//        calculateHowFarToTheTarget(myLat, myLon, dto);
//
//        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
//                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());
    }

    @Test
    @DisplayName("MBR Contains")
    void mbr() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresByMbrContains(dist, myLat, myLon);
    }

    @Test
    @DisplayName("St_Contains")
    void st_contains() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresBySTContains(dist, myLat, myLon);
    }

    @Test
    @DisplayName("St_Contains With circle")
    void st_contains_circle() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresBySTContainsWithCircle(2, myLat, myLon);
        System.out.println("result.size() = " + result.size());
    }

    @Test
    @DisplayName("ST_Contains QueryDSL")
    void st_contains_querydsl() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;

        List<Store> result = storeService.getStoresByStContains(myLat, myLon);
    }


    @Test
    @DisplayName("WKT 읽기")
    void wktReader_test() throws ParseException {
        String pointFormat = String.format("POINT(%f %f)", 35.1710366410643, 129.175759994618);
        String lineStringFormat = String.format("LINESTRING(%f %f, %f %f)", 35.182416023937336, 129.20790463400292, 35.14426110121965, 129.16123271344156);
        String polygonFormat = String.format("POLYGON((%f %f, %f %f, %f %f))", 35.182416023937336, 129.20790463400292, 35.14426110121965, 129.16123271344156, 35.182416023937336, 129.20790463400292);

        Geometry point = wktToGeometry(pointFormat);
        Geometry lineString = wktToGeometry(lineStringFormat);
        // polygon : startPoint와 endPoint가 일치해야만 함
        Geometry polygon = wktToGeometry(polygonFormat);

        assertThat(point.getGeometryType()).isEqualTo("Point");
        assertThat(lineString.getGeometryType()).isEqualTo("LineString");
        assertThat(polygon.getGeometryType()).isEqualTo("Polygon");
    }

    private Geometry wktToGeometry(String text) throws ParseException {
        return new WKTReader().read(text);
    }
}
