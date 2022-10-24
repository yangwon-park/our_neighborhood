package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EditForm {

    private Long id;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

}
