package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class
FindPasswordForm {

    @NotBlank
    private String userId;

    @Email
    private String email;
}
