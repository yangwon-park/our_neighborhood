package ywphsm.ourneighbor.repository.store.dto;

import lombok.Data;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

// 메뉴 없이
// 최소한의 기본 정보만 노출 => 홈화면에서 검색 시 보여줄 DTO
@Data
public class SimpleSearchStoreDTO {

    private Long storeId;
    private String name;
    private Double lon;
    private Double lat;
    private String phoneNumber;
    private StoreStatus status;
    private Address address;

    public SimpleSearchStoreDTO(Store store) {
        storeId = store.getId();
        name = store.getName();
        lon = store.getLon();
        lat = store.getLat();
        phoneNumber = store.getPhoneNumber();
        status = store.getStatus();
        address = store.getAddress();
    }
}
