package ywphsm.ourneighbor.domain.store.days;

import lombok.*;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DaysOfStore {

    @Id
    @GeneratedValue
    @Column(name = "days_of_store_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "days_id")
    private Days days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull
    private String daysName;

    /*
        Store와 매핑할 때 사용
     */
    public DaysOfStore(Days days, Store store) {
        this.days = days;
        this.daysName = days.getType().getDescription();
        this.store = store;
    }

    @Builder
    public DaysOfStore(Long id, Days days, Store store, String daysName) {
        this.id = id;
        this.days = days;
        this.store = store;
        this.daysName = daysName;
    }

    public void updateDays(Days days) {
        this.days = days;
        this.daysName = days.getType().getDescription();
    }

    public static void linkDaysAndStore(Days days, Store store) {
        DaysOfStore daysOfStore = new DaysOfStore(days, store);
        days.getDaysOfStoreList().add(daysOfStore);
        store.getDaysOfStoreList().add(daysOfStore);
    }
}
