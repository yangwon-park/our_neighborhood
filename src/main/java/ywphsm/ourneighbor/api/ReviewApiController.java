package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.service.ReviewService;

import javax.validation.Valid;
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

    @PostMapping("/user/review")
    public void save(@Valid ReviewDTO.Add dto, @RequestParam String hashtag) throws IOException, ParseException, InterruptedException {
        reviewService.save(dto, hashtag);
    }

    @DeleteMapping("/review/delete/{storeId}")
    public Long delete(@PathVariable Long storeId, @RequestParam Long reviewId) {
        return reviewService.delete(storeId, reviewId);
    }
}