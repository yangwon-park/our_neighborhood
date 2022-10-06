package ywphsm.ourneighbor.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReviewDTO {

    @Data
    @NoArgsConstructor
    public static class Detail {

        private Long id;

        private String content;

        private Integer rating;

        private LocalDateTime createDate;

        public Detail(Review review) {
            id = review.getId();
            content = review.getContent();
            rating = review.getRating();
            createDate = review.getCreatedDate();
        }
    }
}
