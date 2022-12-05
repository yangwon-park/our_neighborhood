package ywphsm.ourneighbor.unit.spatial;

import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.store.distance.Distance;
import ywphsm.ourneighbor.domain.store.distance.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static ywphsm.ourneighbor.domain.store.distance.Direction.*;
import static ywphsm.ourneighbor.domain.store.distance.Direction.SOUTHEAST;
import static ywphsm.ourneighbor.domain.store.distance.Distance.calculatePoint;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeolatteTest {

    @Test
    @DisplayName("WKT 읽기")
    void fromWktTest() {
        String pointFormat = String.format("POINT(%f %f)", 129.175759994618, 35.1710366410643);
        String lineStringFormat = String.format("LINESTRING(%f %f, %f %f)", 129.20790463400292, 35.182416023937336, 129.16123271344156, 35.14426110121965);
        String polygonFormat = String.format("POLYGON((%f %f, %f %f, %f %f))", 129.20790463400292, 35.182416023937336, 129.16123271344156, 35.14426110121965, 129.20790463400292, 35.182416023937336);

        Geometry<?> point = Wkt.fromWkt(pointFormat);
        Geometry<?> lineString = Wkt.fromWkt(lineStringFormat);
        Geometry<?> polygon = Wkt.fromWkt(polygonFormat);

        assertThat(point.getGeometryType()).isEqualTo(GeometryType.POINT);
        assertThat(lineString.getGeometryType()).isEqualTo(GeometryType.LINESTRING);
        assertThat(polygon.getGeometryType()).isEqualTo(GeometryType.POLYGON);
    }

    @Test
    @DisplayName("DSL 사용")
    void dslTest() {
        Point<G2D> point = point(WGS84, g(4.33, 53.21));
        LineString<G2D> lineString = linestring(WGS84, g(4.43, 53.21), g(4.44, 53.20), g(4.45, 53.19));
        Polygon<G2D> polygon = polygon(WGS84, ring(g(4.43, 53.21), g(4.44, 53.22), g(4.43, 53.21)));

        assertThat(point.getGeometryType()).isEqualTo(GeometryType.POINT);
        assertThat(lineString.getGeometryType()).isEqualTo(GeometryType.LINESTRING);
        assertThat(polygon.getGeometryType()).isEqualTo(GeometryType.POLYGON);
    }

    @Test
    @DisplayName("거리 계산")
    void calculateDist() {
        double myLat = 35.1633408;
        double myLon = 129.1845632;

        double dist = Distance.calculateDistByHaversine(35.182416023937336, 129.20790463400292,
                myLat, myLon);

        double refineDist = Math.ceil(dist * 10) / 10.0;

        assertThat(refineDist).isEqualTo(3);
    }
}
