package ywphsm.ourneighbor.api.dto;

import lombok.Getter;

@Getter
public enum SkyStatus {

    SUNNY("맑음"), CLOUDY("구름 많음"), VERYCLOUDY("흐림"),
    RAINY("비"), SNOWY("눈");

    private final String description;

    SkyStatus(String description) {
        this.description = description;
    }
}
