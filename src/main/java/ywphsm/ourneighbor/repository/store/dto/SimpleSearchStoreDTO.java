package ywphsm.ourneighbor.repository.store.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.embedded.Address;
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

    private Double distance;

    private int average;

    private String uploadImgUrl;

    @Builder
    public SimpleSearchStoreDTO(Store store) {
        storeId = store.getId();
        name = store.getName();
        lon = store.getLon();
        lat = store.getLat();
        phoneNumber = store.getPhoneNumber();
        status = store.getStatus();
        address = store.getAddress();
        average = store.getRatingTotal();

        if (store.getFile() != null) {
            uploadImgUrl = store.getFile().getUploadImageUrl();
        }
    }

    public SimpleSearchStoreDTO(Long storeId, String name,
                                Double lon, Double lat, int average,
                                String phoneNumber, StoreStatus status,
                                Address address, String uploadImgUrl) {
        this.storeId = storeId;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.average = average;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.address = address;
        this.uploadImgUrl = uploadImgUrl;
    }
}
