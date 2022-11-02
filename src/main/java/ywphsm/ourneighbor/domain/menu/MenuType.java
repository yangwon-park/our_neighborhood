package ywphsm.ourneighbor.domain.menu;

import lombok.Getter;
import ywphsm.ourneighbor.domain.member.Role;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MenuType {

    MAIN("메인"), SIDE("사이드"), DESSERT("디저트"),
    BEVERAGE("음료"), DRINK("주류"), ADD("추가"), MENU("메뉴판");

    private final String description;

    MenuType(String description) {
        this.description = description;
    }
}
