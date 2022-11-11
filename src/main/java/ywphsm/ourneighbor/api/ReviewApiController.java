package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.service.ReviewService;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewApiController {

    private final ReviewService reviewService;

    @GetMapping("/review/more")
    public Slice<ReviewMemberDTO> more(Long storeId, int page) {
        return reviewService.pagingReview(storeId, page);
    }

    // hastag는 Store에 연관돼있음
    @PostMapping("/user/review")
    public Long save(ReviewDTO.Add dto, @RequestParam String hashtag) throws IOException, ParseException {
        return reviewService.save(dto, hashtag);
    }

    @DeleteMapping("/review/delete/{storeId}")
    public Long delete(@PathVariable Long storeId, @RequestParam Long reviewId) {
        return reviewService.delete(storeId, reviewId);
    }
}