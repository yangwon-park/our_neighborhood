package ywphsm.ourneighbor.repository.review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;

public interface ReviewRepositoryCustom {

    Slice<ReviewMemberDTO> ReviewPage(Pageable pageable, Long storeId);
}
