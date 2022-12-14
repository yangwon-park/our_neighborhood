package ywphsm.ourneighbor.repository.store.days;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;

import java.util.List;

import static ywphsm.ourneighbor.domain.store.days.QDaysOfStore.*;

@RequiredArgsConstructor
public class DaysRepositoryImpl implements DaysRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByDaysIdLinkedDaysOfStore(Long daysId) {
        queryFactory
                .delete(daysOfStore)
                .where(daysOfStore.days.id.eq(daysId))
                .execute();
    }

    @Override
    public void deleteByStoreId(Long storeId) {
        queryFactory
                .delete(daysOfStore)
                .where(daysOfStore.store.id.eq(storeId))
                .execute();
    }

    @Override
    public List<DaysOfStore> getDaysByStoreId(Long storeId) {
        return queryFactory
                .select(daysOfStore)
                .from(daysOfStore)
                .where(daysOfStore.store.id.eq(storeId))
                .fetch();
    }
}
