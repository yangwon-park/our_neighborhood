package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PasswordEditForm {

    @NotBlank
    private String beforePassword;

    @NotBlank
    private String afterPassword;

    @NotBlank
    private String passwordCheck;
}
