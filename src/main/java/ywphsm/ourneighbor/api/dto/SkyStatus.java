package ywphsm.ourneighbor.api.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum SkyStatus {

    SUNNY("SUNNY", "맑음"), CLOUDY("CLOUDY", "구름 많음"),
    VERYCLOUDY("VERYCLOUDY", "흐림"),
    RAINY("RAINY", "비"), SNOWY("SNOWY", "눈");

    private final String key;

    private final String description;

    SkyStatus(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /*
        UnmodifiableMap : read-only Map
        Map<Role Title, Role Name>
        static 객체 => 앱 초기 구동 시 1회만 수행됨
     */
    private static final Map<String, String> code = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(SkyStatus::getKey, SkyStatus::name))
    );

    public static SkyStatus of(final String key) {
        return SkyStatus.valueOf(code.get(key));
    }
}
