package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.store.Store;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class StoreAddDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String zipcode;
    @NotBlank
    private String roadAddr;
    @NotBlank
    private String numberAddr;
    private String detail;
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
    private Integer offDay;

    @Builder
    public StoreAddDTO(Store store) {
        name = store.getName();
        phoneNumber = store.getPhoneNumber();
        openingTime = store.getOpeningTime();
        closingTime = store.getClosingTime();
        breakStart = store.getBreakStart();
        breakEnd = store.getBreakEnd();
        notice = store.getNotice();
        intro = store.getIntro();
        offDay = store.getOffDay();
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
                .breakStart(breakStart)
                .breakEnd(breakEnd)
                .notice(notice)
                .intro(intro)
                .offDay(offDay)
                .address(new Address(zipcode, roadAddr, numberAddr, detail))
                .build();
    }
}
