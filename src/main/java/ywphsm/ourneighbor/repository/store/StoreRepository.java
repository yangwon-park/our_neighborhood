package ywphsm.ourneighbor.repository.store;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
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
    List<Store> getTopNByCategories(@Param("lineString") Geometry<G2D> lineString, @Param("categoryId") Long categoryId) throws ParseException;


    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where s.id < :range")
    List<Store> findAllStoresLt(@Param("range") Long range);

    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where mbrcontains(:lineString, s.point) = true and s.id < 20000000")
    List<Store> getStoresByMbrContains(@Param("lineString") Geometry<G2D> lineString) throws ParseException;

    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where st_contains(:polygon, s.point) = true and s.id < 20000000")
    List<Store> getStoresBySTContains(@Param("polygon") Geometry<G2D> polygon) throws ParseException;

    @Query("select s from Store s " +
            "left outer join fetch s.file " +
            "where st_contains(:circle, s.point) = true and s.id < 20000000")
    List<Store> getStoresBySTContainsWithCircle(@Param("circle") Geometry<?> circle) throws ParseException;
}