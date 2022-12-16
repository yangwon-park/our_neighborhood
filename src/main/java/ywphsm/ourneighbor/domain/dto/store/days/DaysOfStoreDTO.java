package ywphsm.ourneighbor.domain.dto.store.days;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;

@Data
public class DaysOfStoreDTO {

    private Long daysId;

    private Long storeId;

    private String daysName;


    /*
        Projection에 사용
     */
    public DaysOfStoreDTO(String daysName) {
        this.daysName = daysName;
    }

    @Builder
    public DaysOfStoreDTO(Long daysId, Long storeId, String daysName) {
        this.daysId = daysId;
        this.storeId = storeId;
        this.daysName = daysName;
    }

    public DaysOfStoreDTO(DaysOfStore daysOfStore) {
        this.daysId = daysOfStore.getDays().getId();
        this.storeId = daysOfStore.getStore().getId();
        this.daysName = daysOfStore.getDays().getType().getDescription();
    }
}
