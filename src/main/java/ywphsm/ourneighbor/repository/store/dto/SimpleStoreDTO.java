package ywphsm.ourneighbor.repository.store.dto;

import lombok.Data;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import java.time.LocalTime;

// 메뉴 없이
// 최소한의 기본 정보만 노출 => 홈화면에서 검색 시 보여줄 DTO
@Data
public class SimpleStoreDTO {

    private Long storeId;
    private String name;
    private Double lon;
    private Double lat;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private Integer offDay;
    private StoreStatus status;
    private Address address;

    public SimpleStoreDTO(Store store) {
        storeId = store.getId();
        name = store.getName();
        lon = store.getLon();
        lat = store.getLat();
        openingTime = store.getOpeningTime();
        closingTime = store.getClosingTime();
        breakStart = store.getBreakStart();
        breakEnd = store.getBreakEnd();
        offDay = store.getOffDay();
        status = store.getStatus();
        address = store.getAddress();
    }
}
