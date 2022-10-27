package ywphsm.ourneighbor.repository.category;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.category.QCategory;

import java.util.List;

import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;

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

    @Override
    public List<Category> findByDepth(Long depth) {
        NumberExpression<Integer> rank = new CaseBuilder()
                .when(category.name.eq("동네 맛집")).then(1)
                .when(category.name.eq("카페")).then(2)
                .when(category.name.eq("인기 술집")).then(3)
                .when(category.name.eq("문화 / 여가")).then(4)
                .otherwise(5);
        
        return queryFactory
                .selectFrom(category)
                .where(category.depth.eq(depth))
                .limit(4)
                .orderBy(rank.asc())
                .fetch();
    }
}

