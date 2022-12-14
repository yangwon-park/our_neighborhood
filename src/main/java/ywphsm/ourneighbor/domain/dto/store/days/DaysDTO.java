package ywphsm.ourneighbor.domain.dto.store.days;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysType;

@Data
public class DaysDTO {

    private Long daysId;

    private DaysType type;

    @Builder
    public DaysDTO(Long daysId, DaysType type) {
        this.daysId = daysId;
        this.type = type;
    }

    public static DaysDTO of(Days entity) {
        return DaysDTO.builder()
                .daysId(entity.getId())
                .type(entity.getType())
                .build();
    }
}
