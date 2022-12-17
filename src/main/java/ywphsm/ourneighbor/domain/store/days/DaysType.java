package ywphsm.ourneighbor.domain.store.days;

import lombok.Getter;

@Getter
public enum DaysType {

    SUN("일요일"), MON("월요일"), TUE("화요일"),
    WED("수요일"), THU("목요일"), FRI("금요일"), SAT("토요일");

    private final String description;

    DaysType(String description) {
        this.description = description;
    }
}
