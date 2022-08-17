package ywphsm.ourneighbor.domain.store.days;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;

//@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DaysOfStore {

    @Id
    @GeneratedValue
    @Column(name = "days_of_store_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "days_id")
    private DaysEntity days;

    public DaysOfStore(DaysEntity days) {
        this.days = days;
    }

    public static DaysOfStore createDaysOfStore(DaysEntity days) {
        return new DaysOfStore(days);
    }
}
