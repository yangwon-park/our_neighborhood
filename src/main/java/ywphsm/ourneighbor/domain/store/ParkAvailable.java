package ywphsm.ourneighbor.domain.store;

import lombok.Getter;

@Getter
public enum ParkAvailable {

    YES("지원됨"), NO("지원 안 됨"),
    NOPARKINGSPACE("주차 공간없음");

    private final String description;

    ParkAvailable(String description) {
        this.description = description;
    }
}
