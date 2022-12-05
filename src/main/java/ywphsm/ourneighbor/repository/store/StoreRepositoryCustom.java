package ywphsm.ourneighbor.repository.store;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> findAllStores();

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    List<Store> searchTopNByCategories(Polygon<G2D> polygon, Long categoryId);

    Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, Geometry<G2D> polygon, Pageable pageable);

    Long countStoreInPolygon(Polygon<G2D> polygon);

    List<SimpleSearchStoreDTO> searchTop7Random(Polygon<G2D> polygon, Pageable pageable);

}