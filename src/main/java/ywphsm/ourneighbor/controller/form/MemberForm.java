package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberForm {

    @NotBlank
    private String username;

    @NotBlank
    private String nickname;

    @NotNull
    private int age;

    @NotNull
    private int gender;

    @NotBlank
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordCheck;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;
}
