package ywphsm.ourneighbor.domain.store.distance;

import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

public class Distance {


    /*
        현재 위치 -> 검색한 장소까지의 거리값 세팅을 위한 메소드
        DTO에 거리값을 세팅해줌
     */
    public static void calculateHowFarToTheTarget(double myLat, double myLon,
                                                   List<SimpleSearchStoreDTO> findDTO) {
        findDTO.forEach(dto -> {
            double dist = Distance.calculateDistByHaversine(dto.getLat(), dto.getLon(),
                    myLat, myLon);

            double refineDist = Math.ceil(dist * 10) / 10.0;

            dto.setDistance(refineDist);
        });
    }

    public static double calculateDistByHaversine(double lat1, double lon1, double lat2, double lon2) {
        double distance;
        double radius = 6371;                   // 지구 반지름
        double toRadian = Math.PI / 180;        // pi * 1 라디안 = 180도

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

    public static Location calculatePoint(double currentLat, double currentLon,
                                     double distance, double angle) {
        double radianLat = toRadian(currentLat);
        double radianLon = toRadian(currentLon);
        double radianAngle = toRadian(angle);
        double distanceRadius = distance / 6371.01;

        double lat = Math.asin(sin(radianLat) * cos(distanceRadius) +
                cos(radianLat) * sin(distanceRadius) * cos(radianAngle));

        double lon = radianLon + Math.atan2(sin(radianAngle) * sin(distanceRadius) *
                cos(radianLat), cos(distanceRadius) - sin(radianLat) * sin(lat));

        lon = normalizeLongitude(lon);

        return new Location(toDegree(lat), toDegree(lon));
    }

    private static double toRadian(double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static double toDegree(double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static double sin(double coordinate) {
        return Math.sin(coordinate);
    }

    private static double cos(double coordinate) {
        return Math.cos(coordinate);
    }

    private static double normalizeLongitude(double longitude) {
        return (longitude + 540) % 360 - 180;
    }
}
