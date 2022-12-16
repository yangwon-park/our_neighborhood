package ywphsm.ourneighbor.repository.category;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ywphsm.ourneighbor.domain.category.Category;

import java.util.List;

import static ywphsm.ourneighbor.domain.category.QCategory.*;
import static ywphsm.ourneighbor.domain.category.QCategoryOfStore.*;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findByDepthCaseByOrderByName(Long depth) {
        NumberExpression<Integer> rank = new CaseBuilder()
                .when(category.name.eq("동네 맛집")).then(1)
                .when(category.name.eq("카페 / 베이커리")).then(2)
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

    @Override
    public void deleteByCategoryLinkedCategoryOfStore(Category category) {
        queryFactory
                .delete(categoryOfStore)
                .where(categoryOfStore.category.eq(category))
                .execute();
    }
}

