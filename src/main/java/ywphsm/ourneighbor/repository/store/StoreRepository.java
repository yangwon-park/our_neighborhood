package ywphsm.ourneighbor.repository.store;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {

    List<Store> findByName(String name);

    @Query("select s from Store s " +
            "join s.categoryOfStoreList cs " +
            "join fetch s.file " +
            "where mbrcontains(:lineString, point(s.lat, s.lon)) = true " +
            "and cs.category.id = :categoryId")
    List<Store> getTopNByCategories(@Param("lineString") LineString<G2D> lineString, @Param("categoryId") Long categoryId) throws ParseException;


    /*
        범위 내의 모든 데이터 조회
     */
    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where s.id >= 14500000 and s.id <= 15500000")
    List<Store> findAllStoresLt(@Param("range") Long range);

    /*
        MBCContains
     */
    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where mbrcontains(:lineString, s.point) = true and s.id >= 14500000 and s.id <= 15500000")
    List<Store> getStoresByMbrContains(@Param("lineString") LineString<G2D> lineString) throws ParseException;

    /*
        ST_Contains (Polygon)
     */
    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where st_contains(:polygon, s.point) = true and s.id >= 14500000 and s.id <= 15500000")
    List<Store> getStoresBySTContains(@Param("polygon") Polygon<G2D> polygon) throws ParseException;

    /*
        ST_Contains (Circle)
     */
    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where st_contains(:circle, s.point) = true and s.id >= 14500000 and s.id <= 15500000")
    List<Store> getStoresBySTContainsWithCircle(@Param("circle") Geometry<?> circle) throws ParseException;
}