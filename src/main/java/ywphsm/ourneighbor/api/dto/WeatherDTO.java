package ywphsm.ourneighbor.api.dto;

import lombok.Data;

@Data
public class WeatherDTO {

    private String POP;         // 강수 확률  (%)
    private String PTY;         // 강수 형태  (0: 없음, 1: 비, 2: 비/눈, 3: 눈, 4: 소나기)
    private String SKY;         // 하늘 상태  (1: 맑음, 3: 구름 많음, 4: 흐림)
    private String TMP;         // 1시간 기온 (섭씨)
    private String PCP;         // 시간당 강수량 (이슬비: 2.5 이하, 약한 비: 2.5, 보통 비: 2.5~7.6, 폭우: 7.6)
    private String pm10Value;   // 미세먼지 농도

    private SkyStatus status;
    private RainKind rainKind;
}
