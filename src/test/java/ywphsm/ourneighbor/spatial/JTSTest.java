package ywphsm.ourneighbor.spatial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class JTSTest {

    @Test
    @DisplayName("WKT 읽기")
    void wktReaderTest() throws ParseException {
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
