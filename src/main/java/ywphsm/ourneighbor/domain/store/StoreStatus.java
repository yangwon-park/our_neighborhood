package ywphsm.ourneighbor.domain.store;


import lombok.Getter;

@Getter
// 현재 가게의 오픈 여부
public enum StoreStatus {
    OPEN("영업중"), CLOSED("영업 안 함"), BREAK("쉬는 시간");

    private final String description;

    StoreStatus(String description) {
        this.description = description;
    }
}
