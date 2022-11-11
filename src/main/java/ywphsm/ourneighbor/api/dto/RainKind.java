package ywphsm.ourneighbor.api.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum RainKind {

    NONE("NONE", "강수없음"), DRIZZLE("DRIZZLE", "이슬비"),
    RAIN("RAIN", "비"), DOWNPOUR("DOWNPOUR", "폭우");

    private final String key;

    private final String description;

    RainKind(String key, String description) {
        this.key = key;
        this.description = description;
    }

    // UnmodifiableMap : read-only Map
    // Map<Role Title, Role Name>
    // static 객체 => 앱 초기 구동 시 1회만 수행됨
    private static final Map<String, String> code = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(RainKind::getKey, RainKind::name))
    );

    public static RainKind of(final String key) {
        return RainKind.valueOf(code.get(key));
    }
}
