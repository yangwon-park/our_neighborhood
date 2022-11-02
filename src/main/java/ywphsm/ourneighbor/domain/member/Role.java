package ywphsm.ourneighbor.domain.member;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Role {

    USER("ROLE_USER", "손님"),
    SELLER("ROLE_SELLER", "판매자"),
    ADMIN("ROLE_ADMIN", "관리자");

    // UnmodifiableMap : read-only Map
    // Map<Role Title, Role Name>
    // static 객체 => 앱 초기 구동 시 1회만 수행됨
    private static final Map<String, String> code = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Role::getTitle, Role::name))
    );

    //security api login에 필요
    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public static Role of(final String keyword) {
        return Role.valueOf(code.get(keyword));
    }
}