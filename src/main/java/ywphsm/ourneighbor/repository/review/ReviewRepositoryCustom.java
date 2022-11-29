package ywphsm.ourneighbor.repository.review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;

import java.util.List;

public interface ReviewRepositoryCustom {

    Slice<ReviewMemberDTO> reviewPage(Pageable pageable, Long storeId);
    List<ReviewMemberDTO> myReview(Long memberId);

    List<String> reviewImageUrl(Long reviewId);

}
