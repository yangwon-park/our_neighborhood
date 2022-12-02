package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewDTO {

    @Data
    @NoArgsConstructor
    public static class Add {

        @NotBlank
        @Size(max = 200)
        private String content;
        @NotNull
        private Integer rating;
        private List<MultipartFile> file;
        private Long storeId;
        private Long memberId;

        @Builder
        public Add(String content, Integer rating, Long storeId, Long memberId, List<MultipartFile> file) {
            this.content = content;
            this.rating = rating;
            this.storeId = storeId;
            this.memberId = memberId;
            this.file = file;
        }

        public Review toEntity(Store store, Member member) {
            return Review.builder()
                    .content(content)
                    .rating(rating)
                    .store(store)
                    .member(member)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Detail {

        private Long id;

        private String content;

        private Integer rating;

        private List<UploadFile> file = new ArrayList<>();

        private LocalDateTime createDate;

        public Detail(Review review) {
            id = review.getId();
            content = review.getContent();
            rating = review.getRating();
            file = review.getFileList();
            createDate = review.getCreatedDate();
        }
    }
}
