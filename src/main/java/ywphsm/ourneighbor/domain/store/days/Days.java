package ywphsm.ourneighbor.domain.store.days;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.dto.store.days.DaysDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Days {

    @Id
    @GeneratedValue
    @Column(name = "days_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DaysType type;

    @OneToMany(mappedBy = "days")
    private List<DaysOfStore> daysOfStoreList = new ArrayList<>();

    @Builder
    public Days(Long id, DaysType type) {
        this.id = id;
        this.type = type;
    }

    public static DaysDTO of(Days days) {
        return DaysDTO.builder()
                .daysId(days.getId())
                .type(days.getType())
                .build();
    }
}
