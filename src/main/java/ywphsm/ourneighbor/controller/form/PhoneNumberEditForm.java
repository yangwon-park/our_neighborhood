package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PhoneNumberEditForm {

    @NotBlank
    @Pattern(regexp = "([0-9]).{10}",
            message = "11자리의 숫자여야합니다")
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "([0-9]).{5}",
            message = "6자리의 숫자여야합니다")
    private String certifiedNumber;
}
