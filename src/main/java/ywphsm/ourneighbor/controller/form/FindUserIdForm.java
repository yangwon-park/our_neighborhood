package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class FindUserIdForm {

    @Email
    @NotNull
    private String email;
}