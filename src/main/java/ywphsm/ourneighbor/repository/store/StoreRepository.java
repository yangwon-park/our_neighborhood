package ywphsm.ourneighbor.repository.store;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {

    List<Store> findByName(String name);

    @Query(value = "SELECT s FROM Store s WHERE within(:point, :bounds) = true")
    List<Store> findAllWithin(@Param("point") Geometry point, @Param("bounds") Geometry bounds);
}