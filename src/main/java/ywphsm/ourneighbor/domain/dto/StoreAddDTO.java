package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ywphsm.ourneighbor.domain.Address;
<<<<<<< HEAD
=======
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
import ywphsm.ourneighbor.domain.store.Store;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
<<<<<<< HEAD
import java.time.LocalDateTime;
import java.time.LocalTime;
=======
import java.time.LocalTime;
import java.util.List;
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c

@Data
@NoArgsConstructor
public class StoreAddDTO {

    @NotBlank
    private String name;
<<<<<<< HEAD
    @NotBlank
    private String zipcode;
    @NotBlank
    private String roadAddr;
    @NotBlank
    private String numberAddr;
    private String detail;
=======

    @NotBlank
    private String zipcode;

    @NotBlank
    private String roadAddr;

    @NotBlank
    private String numberAddr;

    private String detail;

    @NotBlank
    private Double lat;

    @NotBlank
    private Double lon;

>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
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
    private String notice;
    private String intro;
<<<<<<< HEAD
    private Integer offDay;
=======
    private List<String> offDays;
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c

    @Builder
    public StoreAddDTO(Store store) {
        name = store.getName();
        phoneNumber = store.getPhoneNumber();
        openingTime = store.getOpeningTime();
        closingTime = store.getClosingTime();
<<<<<<< HEAD
=======
        lat = store.getLat();
        lon = store.getLon();
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
        breakStart = store.getBreakStart();
        breakEnd = store.getBreakEnd();
        notice = store.getNotice();
        intro = store.getIntro();
<<<<<<< HEAD
        offDay = store.getOffDay();
=======
        offDays = store.getOffDays();
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
        zipcode = store.getAddress().getZipcode();
        roadAddr = store.getAddress().getRoadAddr();
        numberAddr = store.getAddress().getNumberAddr();
        detail = store.getAddress().getDetail();
    }

    public Store toEntity() {
        return Store.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .openingTime(openingTime)
                .closingTime(closingTime)
<<<<<<< HEAD
=======
                .lat(lat)
                .lon(lon)
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
                .breakStart(breakStart)
                .breakEnd(breakEnd)
                .notice(notice)
                .intro(intro)
<<<<<<< HEAD
                .offDay(offDay)
=======
                .offDays(offDays)
>>>>>>> 130a377259d58076f245b658a1838abb3d42525c
                .address(new Address(zipcode, roadAddr, numberAddr, detail))
                .build();
    }
}
