package ywphsm.ourneighbor.api.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum RecommendKind {

    BADPM("BADPM", "미세먼지 안 좋은 날"), RAINY("RAINY", "비오는 날"),
    DOWNPOUR("DOWNPOUR", "폭우"), SNOWY("SNOWY", "눈오는 날"),
    VERYCLOUDY("VERYCLOUDY", "매우 흐린날"), COLD("COLD", "추운 날"),
    SUNNYANDGOODPM("SUNNYANDGOODPM", "맑고 미세먼지 없는 날"), NORMAL("NORMAL", "평범한 날");

    private final String key;

    private final String description;

    RecommendKind(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /*
        UnmodifiableMap : read-only Map
        Map<Role Title, Role Name>
        static 객체 => 앱 초기 구동 시 1회만 수행됨
     */
    private static final Map<String, String> code = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(RecommendKind::getKey, RecommendKind::name))
    );

    public static RecommendKind of(final String key) {
        return RecommendKind.valueOf(code.get(key));
    }
}
