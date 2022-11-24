package ywphsm.ourneighbor.repository.store;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> findAllStores();

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    List<Store> getTopNByCategories(Polygon<G2D> lineString, Long categoryId);

    Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, Geometry<G2D> polygon, Pageable pageable);
}