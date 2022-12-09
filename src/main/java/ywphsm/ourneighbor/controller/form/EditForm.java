package ywphsm.ourneighbor.controller.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.file.UploadFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EditForm {

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    private String imgUrl;

    private MultipartFile file;

}
