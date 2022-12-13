package ywphsm.ourneighbor.repository.store.days;

import ywphsm.ourneighbor.domain.store.days.DaysOfStore;

import java.util.List;

public interface DaysRepositoryCustom {

    List<DaysOfStore> getDaysByStoreId(Long storeId);

    void deleteByDaysIdLinkedDaysOfStore(Long daysId);

    void deleteByStoreId(Long storeId);
}

