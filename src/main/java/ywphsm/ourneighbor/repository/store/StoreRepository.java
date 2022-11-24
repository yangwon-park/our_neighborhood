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



}