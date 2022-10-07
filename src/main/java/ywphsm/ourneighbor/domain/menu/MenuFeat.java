package ywphsm.ourneighbor.domain.menu;

import lombok.Getter;

@Getter
public enum MenuFeat {

    NORMAL("없음"),
    SPICY("매움"), BEST("인기"),
    SIGN("대표"), SET("세트");

    private final String description;

    MenuFeat(String description) {
        this.description = description;
    }
}
