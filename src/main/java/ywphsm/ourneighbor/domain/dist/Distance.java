package ywphsm.ourneighbor.domain.dist;

public class Distance {

    public static double byHaversine(double lat1, double lon1, double lat2, double lon2) {
        double distance;
        double radius = 6371;             // 지구 반지름
        double toRadian = Math.PI / 180;  // pi * 1 라디안 = 180도

        double deltaLatitude = Math.abs(lat1 - lat2) * toRadian;
        double deltaLongitude = Math.abs(lon1 - lon2) * toRadian;

        double sinDeltaLat = Math.sin(deltaLatitude / 2);
        double sinDeltaLng = Math.sin(deltaLongitude / 2);
        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(lat1 * toRadian) * Math.cos(lat2 * toRadian) * sinDeltaLng * sinDeltaLng
        );

        distance = 2 * radius * Math.asin(squareRoot);

        return distance;
    }
}
