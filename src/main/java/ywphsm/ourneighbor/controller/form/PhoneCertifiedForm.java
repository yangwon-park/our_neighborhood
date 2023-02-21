package ywphsm.ourneighbor.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PhoneCertifiedForm {

    @NotBlank
    @Pattern(regexp = "([0-9]).{10}",
            message = "11자리의 숫자여야합니다")
    private String phoneNumber;

    private String certifiedNumber;

    public PhoneCertifiedForm(String phoneNumber, String certifiedNumber) {
        this.phoneNumber = phoneNumber;
        this.certifiedNumber = certifiedNumber;
    }
}
