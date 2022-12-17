package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.spatial.GeometryExpressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.*;
import org.springframework.data.domain.*;
import ywphsm.ourneighbor.domain.dto.store.days.DaysOfStoreDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.days.QDaysOfStore;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

import static com.querydsl.core.types.Projections.*;
import static org.springframework.util.StringUtils.*;
import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;
import static ywphsm.ourneighbor.domain.file.QUploadFile.*;
import static ywphsm.ourneighbor.domain.hashtag.QHashtag.*;
import static ywphsm.ourneighbor.domain.hashtag.QHashtagOfStore.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;
import static ywphsm.ourneighbor.domain.store.days.QDaysOfStore.*;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> searchByKeyword(String keyword) {
        return queryFactory
                .select(store)
                .from(store)
                .where(keywordContains(keyword))
                .fetch();
    }

    @Override
    public List<Store> searchByCategory(Long categoryId) {
        return queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.categoryOfStoreList, categoryOfStore)
                .innerJoin(categoryOfStore.category, category)
                .innerJoin(store.file, uploadFile)
                .fetchJoin()
                .where(categoryOfStore.category.id.eq(categoryId))
                .fetch();
    }

    @Override
    public List<Store> searchTopNByCategories(Polygon<G2D> polygon, Long categoryId) {
        return queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.categoryOfStoreList, categoryOfStore)
                .innerJoin(categoryOfStore.category, category)
                .innerJoin(store.file, uploadFile)
                .fetchJoin()
                .where(stContains(polygon), stDistance(polygon).loe(3), categoryOfStore.category.id.eq(categoryId))
                .fetch();
    }


    /*
        Projections 참고
            https://wildeveloperetrain.tistory.com/94
        Geolatte Geom 참고
            https://github.com/GeoLatte/geolatte-geom
     */
    @Override
    public Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, Geometry<G2D> polygon, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        buildHashtagIdEq(hashtagIdList, builder);

        List<SimpleSearchStoreDTO> list = queryFactory
                .select(constructor(SimpleSearchStoreDTO.class,
                        store.id, store.name, store.lon, store.lat, store.phoneNumber, store.status,
                        store.businessTime, store.address, store.ratingTotal, store.file.uploadImageUrl,
                        list(constructor(DaysOfStoreDTO.class,
                                daysOfStore.daysName))
                ))
                .from(store)
                .leftJoin(store.daysOfStoreList, daysOfStore).distinct()
                .innerJoin(store.hashtagOfStoreList, hashtagOfStore)
                .innerJoin(hashtagOfStore.hashtag, hashtag)
                .where(builder)
                .where(stContains(polygon), stDistance(polygon).loe(3))
                .groupBy(store.name)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (list.size() > pageable.getPageSize()) {
            list.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(list, pageable, hasNext);
    }

    @Override
    public List<SimpleSearchStoreDTO> searchTop7Random(Polygon<G2D> polygon, Pageable pageable) {
        final int dist = 3;

        return queryFactory
                .select(constructor(SimpleSearchStoreDTO.class,
                        store.id, store.name, store.lon, store.lat, store.phoneNumber,
                        store.status, store.businessTime, store.address, store.ratingTotal, store.file.uploadImageUrl
                )).distinct()
                .from(store)
                .where(stContains(polygon), stDistance(polygon).loe(dist))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long countStoreInPolygon(Polygon<G2D> polygon) {
        return queryFactory
                .select(store.count())
                .from(store)
                .where(store.file.uploadImageUrl.isNotNull())
                .where(stContains(polygon), stDistance(polygon).loe(3))
                .fetchOne();
    }

    @Override
    public List<Store> findAllStoresJoinUploadFileFetchJoin() {
        return queryFactory
                .select(store)
                .from(store)
                .leftJoin(store.file, uploadFile)
                .fetchJoin()
                .fetch();
    }

    /*
        BooleanExpression
     */
    private BooleanExpression stContains(Geometry<?> polygon) {
        return GeometryExpressions
                .asGeometry(polygon)
                .contains(store.point);
    }

    private NumberExpression<Double> stDistance(Geometry<G2D> polygon) {
        return GeometryExpressions
                .asGeometry(store.point)
                .distance(polygon);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!hasText(keyword)) {
            return null;
        }

        return store.name.contains(keyword);
    }


    private void buildHashtagIdEq(List<Long> hashtagIdList, BooleanBuilder builder) {
        if (hashtagIdList.isEmpty()) {
            return;
        }

        for (Long id : hashtagIdList) {
            builder.or(hashtagOfStore.hashtag.id.eq(id).and(hashtagOfStore.store.id.eq(store.id)));
        }
    }
}