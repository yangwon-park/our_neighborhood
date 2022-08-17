package ywphsm.ourneighbor.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Embeddable;

// 임베디드 타입
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String roadAddr;      // 해운대구 좌동 ~~
    private String numberAddr;
    private String zipcode;     // 우편 번호
    private String detail;

    public Address(String roadAddr, String numberAddr, String zipcode, String detail) {
        this.roadAddr = roadAddr;
        this.numberAddr = numberAddr;
        this.zipcode = zipcode;
        this.detail = detail;
    }
}
