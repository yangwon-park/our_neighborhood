package ywphsm.ourneighbor.domain.store.days;

import lombok.Getter;

@Getter
public enum DaysType {

    SUN("일"), MON("월"), TUE("화"),
    WED("수"), THU("목"), FRI("금"), SAT("토");

    private final String description;

    DaysType(String description) {
        this.description = description;
    }
}
