package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.util.StringUtils;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.distance.Direction;
import ywphsm.ourneighbor.domain.store.distance.Distance;
import ywphsm.ourneighbor.domain.store.distance.Location;

import javax.persistence.EntityManager;
import java.util.List;

import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;
import static ywphsm.ourneighbor.domain.store.distance.Distance.*;

@Slf4j
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> searchByName(StoreSearchCond cond) {
        return queryFactory
                .select(store)
                .from(store)
                .where(nameContains(cond.getKeyword()))
                .fetch();
    }

    @Override
    public List<Store> searchByKeyword(String keyword) {
        return queryFactory
                .select(store)
                .from(store)
                .where(nameContains(keyword))
                .fetch();
    }

    @Override
    public List<Store> searchByCategory(Long categoryId) {
        return queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.categoryOfStoreList, categoryOfStore)
                .innerJoin(categoryOfStore.category, category)
                .where(categoryOfStore.category.id.eq(categoryId), categoryOfStore.store.id.eq(store.id))
                .fetch();
    }

    // 참고
    // https://www.baeldung.com/hibernate-spatial
    @Override
    public List<Store> getTop5ByCategories(Long categoryId, double dist, double lat, double lon) throws ParseException {
        Location northEast = calculatePoint(lat, lon, dist, Direction.NORTHEAST.getAngle());
        Location southWest = calculatePoint(lat, lon, dist, Direction.SOUTHWEST.getAngle());

        double nex = northEast.getLat();
        double ney = northEast.getLon();
        double swx = southWest.getLat();
        double swy = southWest.getLon();

        String lineStringFormat = String.format("LINESTRING(%f %f, %f %f)", nex, ney, swx, swy);

        Geometry lineString = wktToGeometry(lineStringFormat);

        return em.createQuery("" +
                        "select s from Store s " +
                        "join s.categoryOfStoreList cs " +
                        "where mbrcontains(:lineString, point(s.lat, s.lon)) = true " +
                        "and cs.category.id = :categoryId", Store.class)
                .setParameter("categoryId", categoryId)
                .setParameter("lineString", lineString)
                .setMaxResults(7)
                .getResultList();
    }

    private Geometry wktToGeometry(String text) throws ParseException {
        return new WKTReader().read(text);
    }

    private BooleanExpression nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return store.name.contains(name);
    }

}