package ywphsm.ourneighbor.controller.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewForm {

    @NotBlank
    @Size(max = 200)
    private String content;

    @NotNull
    private Integer rating;

    private MultipartFile file;

}
