package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.ReviewService;
import ywphsm.ourneighbor.service.StoreService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping("/user/review")
    public Long save(ReviewDTO.Add dto) throws IOException {

        log.info("dto={}", dto);
        log.info("dto={}", dto.getFile());

        return reviewService.save(dto);
    }

    @DeleteMapping("/review/delete/{storeId}")
    public Long delete(@PathVariable Long storeId, @RequestParam Long reviewId) {

        log.info("reviewId={}", reviewId);

        return reviewService.delete(storeId, reviewId);
    }

    @GetMapping("/review/more")
    public Slice<ReviewMemberDTO> more(Long storeId, int page) {
        return reviewService.pagingReview(storeId, page);
    }

}
