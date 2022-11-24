package ywphsm.ourneighbor.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.RequestAddStore;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
public class RequestAddStoreDTO {

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

        private Member member;

        @Builder
        public Add(String name, String zipcode, String roadAddr, String numberAddr, Member member) {
            this.name = name;
            this.zipcode = zipcode;
            this.roadAddr = roadAddr;
            this.numberAddr = numberAddr;
            this.member = member;
        }

        public RequestAddStore toEntity() {
            return RequestAddStore.builder()
                    .name(name)
                    .address(new Address(roadAddr, numberAddr, zipcode, null))
                    .member(member)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Detail {

        private Long id;

        private String name;

        private String zipcode;

        private String roadAddr;

        private String numberAddr;

        private String email;

        @QueryProjection
        public Detail(Long id, String name, String zipcode, String roadAddr, String numberAddr, String email) {
            this.id = id;
            this.name = name;
            this.zipcode = zipcode;
            this.roadAddr = roadAddr;
            this.numberAddr = numberAddr;
            this.email = email;
        }


    }
}
