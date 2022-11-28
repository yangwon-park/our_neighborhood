package ywphsm.ourneighbor.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


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

    private List<String> uploadImgUrl;

    @QueryProjection
    public ReviewMemberDTO(Long reviewId, String content, Integer rating,
                           LocalDateTime createDate, Long memberId, String username) {
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.createDate = createDate;
        this.memberId = memberId;
        this.username = username;
    }

    @QueryProjection
    public ReviewMemberDTO(Long reviewId, String content, Integer rating,
                           LocalDateTime createDate, String storeName, Long storeId) {
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.createDate = createDate;
        this.storeName = storeName;
        this.storeId = storeId;
    }



}
