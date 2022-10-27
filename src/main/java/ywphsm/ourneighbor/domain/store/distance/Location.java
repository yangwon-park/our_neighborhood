package ywphsm.ourneighbor.domain.store.distance;

import lombok.Getter;

@Getter
public class Location {

    private Double lat;

    private Double lon;

    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
