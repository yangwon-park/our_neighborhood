package ywphsm.ourneighbor.domain.embedded;

import lombok.*;

import javax.persistence.Embeddable;

// 임베디드 타입
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {
    private String roadAddr;
    private String numberAddr;
    private String zipcode;
    private String detail;
}
