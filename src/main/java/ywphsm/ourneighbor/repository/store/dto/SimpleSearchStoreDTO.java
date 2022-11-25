package ywphsm.ourneighbor.repository.store.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimpleSearchStoreDTO {

    private Long storeId;

    private String name;

    private Double lon;

    private Double lat;

    private String phoneNumber;

    private StoreStatus status;

    private BusinessTime businessTime;

    private List<String> offDays = new ArrayList<>();

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
        businessTime = store.getBusinessTime();
        offDays = store.getOffDays();
        address = store.getAddress();
        average = store.getRatingTotal();

        if (store.getFile() != null) {
            uploadImgUrl = store.getFile().getUploadImageUrl();
        }
    }

    public SimpleSearchStoreDTO(Long storeId, String name, Double lon, Double lat,
                                String phoneNumber, StoreStatus status,
                                Address address, int average, String uploadImgUrl) {
        this.storeId = storeId;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.address = address;
        this.average = average;
        this.uploadImgUrl = uploadImgUrl;
    }

    public SimpleSearchStoreDTO(Long storeId, String name, Double lon, Double lat,
                                String phoneNumber, StoreStatus status, BusinessTime businessTime,
                                Address address, int average, String uploadImgUrl) {
        this.storeId = storeId;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.businessTime = businessTime;
        this.address = address;
        this.average = average;
        this.uploadImgUrl = uploadImgUrl;
    }
}
