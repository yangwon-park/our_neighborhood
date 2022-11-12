package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ywphsm.ourneighbor.domain.store.Store;

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

    @Override
    public Slice<Store> searchByHashtag(Long hashtagId, Pageable pageable) {
        List<Store> list = queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.hashtagOfStoreList, hashtagOfStore)
                .innerJoin(hashtagOfStore.hashtag, hashtag)
                .where(hashtagOfStore.hashtag.id.eq(hashtagId), hashtagOfStore.store.id.eq(store.id))
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
}