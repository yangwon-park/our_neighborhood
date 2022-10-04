package ywphsm.ourneighbor.domain.embedded;

<<<<<<< HEAD
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
=======
import lombok.*;
>>>>>>> main

import javax.persistence.Embeddable;

// 임베디드 타입
@Getter
<<<<<<< HEAD
=======
@ToString
@AllArgsConstructor
>>>>>>> main
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {
    private String roadAddr;
    private String numberAddr;
    private String zipcode;
    private String detail;
<<<<<<< HEAD

    public Address(String roadAddr, String numberAddr, String zipcode, String detail) {
        this.roadAddr = roadAddr;
        this.numberAddr = numberAddr;
        this.zipcode = zipcode;
        this.detail = detail;
    }
=======
>>>>>>> main
}
