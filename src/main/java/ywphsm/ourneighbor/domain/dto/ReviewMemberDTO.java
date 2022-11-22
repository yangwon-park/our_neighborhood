package ywphsm.ourneighbor.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
public class ReviewMemberDTO {

    private Long reviewId;

    private String content;

    private Integer rating;

    private LocalDateTime createDate;

    private Long memberId;

    private String username;

    private String storedFileName;

    private String storeName;

    private Long storeId;

    private String uploadImgUrl;

    @QueryProjection
    public ReviewMemberDTO(Long reviewId, String content, Integer rating,
                           LocalDateTime createDate, Long memberId, String username,
                           String uploadImgUrl) {
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.createDate = createDate;
        this.memberId = memberId;
        this.username = username;
        this.uploadImgUrl = uploadImgUrl;
    }

    @QueryProjection
    public ReviewMemberDTO(Long reviewId, String content, Integer rating,
                           LocalDateTime createDate, String uploadImgUrl, String storeName, Long storeId) {
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.createDate = createDate;
        this.uploadImgUrl = uploadImgUrl;
        this.storeName = storeName;
        this.storeId = storeId;
    }



}
