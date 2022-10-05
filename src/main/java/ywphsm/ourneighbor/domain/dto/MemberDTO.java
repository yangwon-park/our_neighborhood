package ywphsm.ourneighbor.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.member.Role;

import javax.validation.constraints.NotBlank;

public class MemberDTO {

    @Data
    @NoArgsConstructor
    public static class SimpleRole {

        @NotBlank
        private Role role;
    }
}
