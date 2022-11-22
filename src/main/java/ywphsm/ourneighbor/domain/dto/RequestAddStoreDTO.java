package ywphsm.ourneighbor.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.RequestAddStore;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RequestAddStoreDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String roadAddr;

    @NotBlank
    private String numberAddr;

    private Member member;

    public RequestAddStore toEntity() {
        return RequestAddStore.builder()
                .name(name)
                .address(new Address(roadAddr, numberAddr, zipcode, null))
                .member(member)
                .build();
    }

    @QueryProjection
    public RequestAddStoreDTO(String name, String zipcode, String roadAddr, String numberAddr, Member member) {
        this.name = name;
        this.zipcode = zipcode;
        this.roadAddr = roadAddr;
        this.numberAddr = numberAddr;
        this.member = member;
    }
}
