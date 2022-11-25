package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.dto.category.CategoryOfStoreDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.store.ParkAvailable;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreDTO {

    @Data
    @NoArgsConstructor
    public static class Add {

        @NotBlank
        private String name;

        @NotBlank
        private String zipcode;

        @NotBlank
        private String roadAddr;

        @NotBlank
        private String numberAddr;

        private String detail;

        @NotNull
        private Double lat;

        @NotNull
        private Double lon;

        private String phoneNumber;

        @NotNull
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime openingTime;            // 여는 시간

        @NotNull
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime closingTime;            // 닫는 시간

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime breakStart;             // 쉬는 시간 시작

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime breakEnd;               // 쉬는 시간 끝

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime lastOrder;              // 쉬는 시간 끝

        private String notice;

        private String intro;

        private List<String> offDays;

        private ParkAvailable park;

        private String parkDetail;

        private String homePage;

        private Long memberId;

        private List<CategoryOfStoreDTO> categoryOfStores;

        @Builder
        public Add(String name, String zipcode, String roadAddr, String numberAddr, String detail,
                   Double lat, Double lon, String phoneNumber, String homePage, LocalTime lastOrder,
                   LocalTime openingTime, LocalTime closingTime, LocalTime breakStart, LocalTime breakEnd,
                   String notice, String intro, List<String> offDays,
                   ParkAvailable park, String parkDetail, Long memberId,
                   List<CategoryOfStoreDTO> categoryOfStores) {
            this.name = name;
            this.zipcode = zipcode;
            this.roadAddr = roadAddr;
            this.numberAddr = numberAddr;
            this.detail = detail;
            this.lat = lat;
            this.lon = lon;
            this.phoneNumber = phoneNumber;
            this.homePage = homePage;
            this.lastOrder = lastOrder;
            this.openingTime = openingTime;
            this.closingTime = closingTime;
            this.breakStart = breakStart;
            this.breakEnd = breakEnd;
            this.notice = notice;
            this.intro = intro;
            this.offDays = offDays;
            this.park = park;
            this.parkDetail = parkDetail;
            this.memberId = memberId;
            this.categoryOfStores = categoryOfStores;
        }

        @Builder
        public Add(Store store) {
            name = store.getName();
            phoneNumber = store.getPhoneNumber();
            lat = store.getLat();
            lon = store.getLon();
            homePage = store.getHomePage();
            openingTime = store.getBusinessTime().getOpeningTime();
            closingTime = store.getBusinessTime().getClosingTime();
            breakStart = store.getBusinessTime().getBreakStart();
            breakEnd = store.getBusinessTime().getBreakEnd();
            lastOrder = store.getBusinessTime().getLastOrder();
            notice = store.getNotice();
            intro = store.getIntro();
            offDays = store.getOffDays();
            zipcode = store.getAddress().getZipcode();
            roadAddr = store.getAddress().getRoadAddr();
            numberAddr = store.getAddress().getNumberAddr();
            detail = store.getAddress().getDetail();
            park = store.getPark();
            parkDetail = store.getParkDetail();
            categoryOfStores = store.getCategoryOfStoreList().stream()
                    .map(CategoryOfStoreDTO::new)
                    .collect(Collectors.toList());
        }

        public Store toEntity() {
            return Store.builder()
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .businessTime(new BusinessTime(openingTime, closingTime, breakStart, breakEnd, lastOrder))
                    .lat(lat)
                    .lon(lon)
                    .homePage(homePage)
                    .notice(notice)
                    .intro(intro)
                    .park(park)
                    .parkDetail(parkDetail)
                    .offDays(offDays)
                    .address(new Address(roadAddr, numberAddr, zipcode, detail))
                    .categoryOfStoreList(new ArrayList<>())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Detail {
        private Long storeId;

        @NotBlank
        private String name;

        private String phoneNumber;

        private String homePage;

        @NotNull
        @DateTimeFormat(pattern = "HH:mm:ss a")
        private LocalTime openingTime;            // 여는 시간

        @NotNull
        @DateTimeFormat(pattern = "HH:mm:ss a")
        private LocalTime closingTime;            // 닫는 시간

        @DateTimeFormat(pattern = "HH:mm:ss a")
        private LocalTime breakStart;             // 쉬는 시간 시작

        @DateTimeFormat(pattern = "HH:mm:ss a")
        private LocalTime breakEnd;               // 쉬는 시간 끝

        @DateTimeFormat(pattern = "HH:mm:ss a")
        private LocalTime lastOrder;               // 라스트 오더

        private String notice;                    // 가게 소식

        private String intro;                     // 가게 소개

        private int average;

        private List<String> offDays;             // 쉬는 날 (0 : 일요일 ~ 6 : 토요일)

        private StoreStatus status;               // 가게 오픈 상황

        private ParkAvailable park;

        private String parkDetail;

        // 주소는 임베디드 타입으로 받음
        @NotBlank
        private String zipcode;

        @NotBlank
        private String roadAddr;

        @NotBlank
        private String numberAddr;

        private String detail;

        private String uploadImgUrl;

        private MultipartFile file;

        private List<CategoryOfStoreDTO> categoryList;

        private List<HashtagOfStoreDTO.Detail> hashtagList;

        @Builder
        public Detail(Store store) {
            storeId = store.getId();
            name = store.getName();
            phoneNumber = store.getPhoneNumber();
            homePage = store.getHomePage();
            openingTime = store.getBusinessTime().getOpeningTime();
            closingTime = store.getBusinessTime().getClosingTime();
            breakStart = store.getBusinessTime().getBreakStart();
            breakEnd = store.getBusinessTime().getBreakEnd();
            lastOrder = store.getBusinessTime().getLastOrder();
            notice = store.getNotice();
            intro = store.getIntro();
            average = store.getRatingTotal();
            offDays = store.getOffDays();
            status = store.getStatus();
            park = store.getPark();
            parkDetail = store.getParkDetail();
            zipcode = store.getAddress().getZipcode();
            roadAddr = store.getAddress().getRoadAddr();
            numberAddr = store.getAddress().getNumberAddr();
            detail = store.getAddress().getDetail();

            if (store.getFile() != null) {
                uploadImgUrl = store.getFile().getUploadImageUrl();
            }

            categoryList = store.getCategoryOfStoreList().stream()
                    .map(CategoryOfStoreDTO::new)
                    .collect(Collectors.toList());
            hashtagList = store.getHashtagOfStoreList().stream()
                    .map(HashtagOfStoreDTO.Detail::new)
                    .collect(Collectors.toList());
        }

        public Store toEntity() {
            return Store.builder()
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .homePage(homePage)
                    .businessTime(new BusinessTime(openingTime, closingTime, breakStart, breakEnd, lastOrder))
                    .notice(notice)
                    .intro(intro)
                    .offDays(offDays)
                    .status(status)
                    .address(new Address(roadAddr, numberAddr, zipcode, detail))
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Update {

        private Long storeId;

        @NotBlank
        private String name;

        private String phoneNumber;

        private String homePage;

        @NotNull
        private Double lat;

        @NotNull
        private Double lon;

        @NotNull
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime openingTime;            // 여는 시간

        @NotNull
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime closingTime;            // 닫는 시간

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime breakStart;             // 쉬는 시간 시작

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime breakEnd;               // 쉬는 시간 끝
        
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime lastOrder;               // 라스트 오더

        private String notice;                    // 가게 소식
        private String intro;                     // 가게 소개

        private List<String> offDays;             // 쉬는 날 (0 : 일요일 ~ 6 : 토요일)

        private ParkAvailable park;

        private String parkDetail;

        @NotBlank
        private String zipcode;

        @NotBlank
        private String roadAddr;

        @NotBlank
        private String numberAddr;

        private String detail;

        private List<CategoryOfStoreDTO> categoryList;

        @Builder
        public Update(String name, String phoneNumber, Double lat, Double lon,
                      String homePage, LocalTime lastOrder,
                      LocalTime openingTime, LocalTime closingTime, LocalTime breakStart, LocalTime breakEnd,
                      String notice, String intro, List<String> offDays,
                      ParkAvailable park, String parkDetail,
                      String zipcode, String roadAddr, String numberAddr, String detail,
                      List<CategoryOfStoreDTO> categoryList) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.lat = lat;
            this.lon = lon;
            this.homePage = homePage;
            this.lastOrder = lastOrder;
            this.openingTime = openingTime;
            this.closingTime = closingTime;
            this.breakStart = breakStart;
            this.breakEnd = breakEnd;
            this.notice = notice;
            this.intro = intro;
            this.offDays = offDays;
            this.park = park;
            this.parkDetail = parkDetail;
            this.zipcode = zipcode;
            this.roadAddr = roadAddr;
            this.numberAddr = numberAddr;
            this.detail = detail;
            this.categoryList = categoryList;
        }

        @Builder
        public Update(Store store) {
            storeId = store.getId();
            name = store.getName();
            phoneNumber = store.getPhoneNumber();
            lat = store.getLat();
            lon = store.getLon();
            homePage = store.getHomePage();
            openingTime = store.getBusinessTime().getOpeningTime();
            closingTime = store.getBusinessTime().getClosingTime();
            breakStart = store.getBusinessTime().getBreakStart();
            breakEnd = store.getBusinessTime().getBreakEnd();
            lastOrder = store.getBusinessTime().getLastOrder();
            notice = store.getNotice();
            intro = store.getIntro();
            offDays = store.getOffDays();
            park = store.getPark();
            parkDetail = store.getParkDetail();
            zipcode = store.getAddress().getZipcode();
            roadAddr = store.getAddress().getRoadAddr();
            numberAddr = store.getAddress().getNumberAddr();
            detail = store.getAddress().getDetail();
            categoryList = store.getCategoryOfStoreList().stream()
                    .map(CategoryOfStoreDTO::new)
                    .collect(Collectors.toList());
        }

        public Store toEntity() {
            return Store.builder()
                    .id(storeId)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .lat(lat)
                    .lon(lon)
                    .homePage(homePage)
                    .businessTime(new BusinessTime(openingTime, closingTime, breakStart, breakEnd, lastOrder))
                    .notice(notice)
                    .intro(intro)
                    .park(park)
                    .parkDetail(parkDetail)
                    .offDays(offDays)
                    .address(new Address(roadAddr, numberAddr, zipcode, detail))
                    .build();
        }

    }
}