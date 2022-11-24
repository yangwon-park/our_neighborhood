package ywphsm.ourneighbor.domain.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherSearchCond {

    private String skyStatus;
    private String pm10Value;
    private String tmp;
    private String pop;

    public WeatherSearchCond(String skyStatus, String pm10Value, String tmp, String pop) {
        this.skyStatus = skyStatus;
        this.pm10Value = pm10Value;
        this.tmp = tmp;
        this.pop = pop;
    }
}
