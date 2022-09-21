package ywphsm.ourneighbor.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ywphsm.ourneighbor.domain.CategoryOfStore;
import ywphsm.ourneighbor.domain.QCategoryOfStore;

import java.util.List;
import java.util.Optional;

import static ywphsm.ourneighbor.domain.QCategoryOfStore.*;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public CategoryOfStore findCategoryOfStore(Long storeId, Long categoryId) {
        return queryFactory
                .select(categoryOfStore)
                .from(categoryOfStore)
                .where((categoryOfStore.store.id.eq(storeId)).and((categoryOfStore.category.id.eq(categoryId))))
                .fetchOne();
    }
}
