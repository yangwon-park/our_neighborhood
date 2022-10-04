package ywphsm.ourneighbor.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;


@Data
public class ReviewMemberDTO {

    private Long reviewId;

    private String content;

    private Integer rating;

    private String createdBy;

    private Long memberId;

    private String username;

    @QueryProjection
    public ReviewMemberDTO(Long reviewId, String content, Integer rating, String createdBy, Long memberId, String username) {
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.createdBy = createdBy;
        this.memberId = memberId;
        this.username = username;
    }

    public ReviewMemberDTO(ReviewMemberDTO reviewMemberDTO) {
        this.reviewId = reviewMemberDTO.getReviewId();
        this.content = reviewMemberDTO.getContent();;
        this.rating = reviewMemberDTO.getRating();;
        this.createdBy = reviewMemberDTO.getCreatedBy();;
        this.memberId = reviewMemberDTO.getMemberId();;
        this.username = reviewMemberDTO.getUsername();;
    }
}
