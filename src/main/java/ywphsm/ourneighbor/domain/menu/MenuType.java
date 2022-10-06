package ywphsm.ourneighbor.domain.menu;

import lombok.Getter;

@Getter
public enum MenuType {

    MAIN("메인"), DESSERT("디저트"),
    BEVERAGE("음료"), DRINK("주류"),
    ADD("추가");

    private final String description;

    MenuType(String description) {
        this.description = description;
    }
}
