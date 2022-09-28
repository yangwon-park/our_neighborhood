package ywphsm.ourneighbor.repository.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import ywphsm.ourneighbor.domain.category.QCategory;
import ywphsm.ourneighbor.domain.category.QCategoryOfStore;
import ywphsm.ourneighbor.domain.file.QUploadFile;
import ywphsm.ourneighbor.domain.menu.QMenu;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;
import java.util.Optional;

import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;
import static ywphsm.ourneighbor.domain.file.QUploadFile.*;
import static ywphsm.ourneighbor.domain.menu.QMenu.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> searchByName(StoreSearchCond cond) {
        return queryFactory
                .select(store)
                .from(store)
//                .innerJoin(store.menuList, menu)
//                .fetchJoin()
                .where(nameContains(cond.getKeyword()))
                .fetch();
    }

    @Override
    public List<Store> searchByKeyword(String keyword) {
        return queryFactory
                .select(store)
                .from(store)
//                .innerJoin(store.menuList, menu)
//                .fetchJoin()
                .where(nameContains(keyword))
                .fetch();
    }

    @Override
    public List<Store> searchByCategory(Long categoryId) {

        return queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.categoryOfStoreList, categoryOfStore)
                .fetchJoin()
                .innerJoin(categoryOfStore.category, category)
                .fetchJoin()
                .where(categoryOfStore.category.id.eq(categoryId), categoryOfStore.store.id.eq(store.id))
                .fetch();
    }

    @Override
    public Optional<Store> findByIdWithFetch(Long storeId) {
        return Optional.ofNullable(queryFactory
                .select(store)
                .from(store)
                .innerJoin(store.menuList, menu)
                .fetchJoin()
                .innerJoin(menu.file, uploadFile)
                .fetchJoin()
                .where(store.id.eq(storeId))
                .fetchOne());
    }

    private BooleanExpression nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        return store.name.contains(name);
    }
}
