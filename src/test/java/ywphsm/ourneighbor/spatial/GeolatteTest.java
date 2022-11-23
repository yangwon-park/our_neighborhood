package ywphsm.ourneighbor.spatial;

import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.*;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static ywphsm.ourneighbor.domain.store.distance.Distance.calculateHowFarToTheTarget;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeolatteTest {

    @Autowired
    StoreService storeService;

    @Autowired
    MemberService memberService;

    @Test
    @Order(1)
    @DisplayName("단순 조회 후 거리 필터")
    void searchByCategory() {
        String myLat = "35.1633408";
        String myLon = "129.1845632";
        double dist = 3000;

        Long range = 20000000L;

        List<Store> storeList = storeService.findAllStores(range);

        List<SimpleSearchStoreDTO> dto = storeList.stream().map(SimpleSearchStoreDTO::new).collect(Collectors.toList());

        calculateHowFarToTheTarget(myLat, myLon, dto);

        List<SimpleSearchStoreDTO> result = dto.stream().filter(simpleSearchStoreDTO
                -> simpleSearchStoreDTO.getDistance() <= dist / 1000).collect(Collectors.toList());

        System.out.println("result.size() = " + result.size());
    }

    @Test
    @Order(2)
    @DisplayName("MBR Contains")
    void mbr() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresByMbrContains(dist, myLat, myLon);
        System.out.println("result.size() = " + result.size());
    }

    @Test
    @Order(3)
    @DisplayName("ST_Contains")
    void st_contains() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresBySTContains(dist, myLat, myLon);
        System.out.println("result.size() = " + result.size());
    }

    @Test
    @Order(4)
    @DisplayName("ST_Contains With circle")
    void st_contains_circle() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;
        double dist = 3;

        List<Store> result = storeService.getStoresBySTContainsWithCircle(0.03, myLat, myLon);

        System.out.println("result.size() = " + result.size());
    }

    @Test
    @Order(5)
    @DisplayName("ST_Contains QueryDSL")
    void st_contains_querydsl() throws ParseException {
        double myLat = 35.1633408;
        double myLon = 129.1845632;

        List<Store> result = storeService.getStoresByStContainsWithQueryDSL(myLat, myLon);
        System.out.println("result.size() = " + result.size());
    }

    @Test
    @DisplayName("WKT 읽기")
    void fromWkt_test() {
        String pointFormat = String.format("POINT(%f %f)", 35.1710366410643, 129.175759994618);
        String lineStringFormat = String.format("LINESTRING(%f %f, %f %f)", 35.182416023937336, 129.20790463400292, 35.14426110121965, 129.16123271344156);
        String polygonFormat = String.format("POLYGON((%f %f, %f %f, %f %f))", 35.182416023937336, 129.20790463400292, 35.14426110121965, 129.16123271344156, 35.182416023937336, 129.20790463400292);

        Geometry<?> point = Wkt.fromWkt(pointFormat);
        Geometry<?> lineString = Wkt.fromWkt(lineStringFormat);
        Geometry<?> polygon = Wkt.fromWkt(polygonFormat);

        assertThat(point.getGeometryType()).isEqualTo(GeometryType.POINT);
        assertThat(lineString.getGeometryType()).isEqualTo(GeometryType.LINESTRING);
        assertThat(polygon.getGeometryType()).isEqualTo(GeometryType.POLYGON);
    }

    @Test
    @DisplayName("DSL 사용")
    void dsl_test() {
        Point<G2D> point = point(WGS84, g(4.33,53.21));
        LineString<G2D> lineString = linestring(WGS84,g(4.43,53.21),g(4.44,53.20),g(4.45,53.19));
        Polygon<G2D> polygon = polygon(WGS84,ring(g(4.43,53.21),g(4.44,53.22),g(4.43,53.21)));

        assertThat(point.getGeometryType()).isEqualTo(GeometryType.POINT);
        assertThat(lineString.getGeometryType()).isEqualTo(GeometryType.LINESTRING);
        assertThat(polygon.getGeometryType()).isEqualTo(GeometryType.POLYGON);
    }
}
