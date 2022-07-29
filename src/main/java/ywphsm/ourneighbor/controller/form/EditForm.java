package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditForm {

    private Long id;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phoneNumber;
}
