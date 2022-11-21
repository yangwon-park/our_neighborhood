package ywphsm.ourneighbor.spatial;

import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GeolatteTest {

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
