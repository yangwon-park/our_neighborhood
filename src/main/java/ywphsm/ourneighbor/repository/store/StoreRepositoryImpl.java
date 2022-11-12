package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

import static org.springframework.util.StringUtils.*;
import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;
import static ywphsm.ourneighbor.domain.hashtag.QHashtag.*;
import static ywphsm.ourneighbor.domain.hashtag.QHashtagOfStore.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@Slf4j
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
                .where(categoryOfStore.category.id.eq(categoryId), categoryOfStore.store.id.eq(store.id))
                .fetch();
    }

//  Projections 참고
//  https://wildeveloperetrain.tistory.com/94
    @Override
    public Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        hashtagIdEq(hashtagIdList, builder);

        List<SimpleSearchStoreDTO> list = queryFactory
                .select(Projections.constructor(SimpleSearchStoreDTO.class,
                        store.id, store.name, store.lon, store.lat, store.ratingTotal,
                        store.phoneNumber, store.status, store.address, store.file.uploadImageUrl
                        )).distinct()
                .from(store)
                .innerJoin(store.hashtagOfStoreList, hashtagOfStore)
                .innerJoin(hashtagOfStore.hashtag, hashtag)
                .where(builder)
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

    private BooleanExpression keywordContains(String keyword) {
        if (!hasText(keyword)) {
            return null;
        }

        return store.name.contains(keyword);
    }

    private void hashtagIdEq(List<Long> hashtagIdList, BooleanBuilder builder) {
        if (hashtagIdList.isEmpty()) {
            return;
        }

        for (Long id : hashtagIdList) {
            builder.or(hashtagOfStore.hashtag.id.eq(id).and(hashtagOfStore.store.id.eq(store.id)));
        }
    }
}