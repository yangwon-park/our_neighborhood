package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<Store> getTop5ByCategories(String categoryId, double dist, double lat, double lon) {
        Location northEast = Distance.calculatePoint(lat, lon, dist, Direction.NORTHEAST.getAngle());
        Location southWest = Distance.calculatePoint(lat, lon, dist, Direction.SOUTHWEST.getAngle());

        double nex = northEast.getLat();
        double ney = northEast.getLon();
        double swx = southWest.getLat();
        double swy = southWest.getLon();

        // Native Query
        String pointFormat = String.format("'LINESTRING(%f %f, %f %f)'", nex, ney, swx, swy);

        return em.createNativeQuery(
                        "select * " +
                                "from store as s " +
                                "join category_of_store as cs on cs.store_id = s.store_id " +
                                "where mbrcontains(" +
                                "ST_LineStringFromText(" + pointFormat + "), " +
                                "POINT(s.lat, s.lon)) " +
                                "and cs.category_id = :categoryId",
                        Store.class)
                .setParameter("categoryId", Long.parseLong(categoryId))
                .setMaxResults(5)
                .getResultList();
    }

    private BooleanExpression nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return store.name.contains(name);
    }
}