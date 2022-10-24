package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@Slf4j
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {


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

    private BooleanExpression nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return store.name.contains(name);
    }
}