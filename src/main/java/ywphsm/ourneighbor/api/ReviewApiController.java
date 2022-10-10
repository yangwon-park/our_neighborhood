package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.service.ReviewService;

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

    @GetMapping("/review/more")
    public Slice<ReviewMemberDTO> more(Long storeId, int page) {
        return reviewService.pagingReview(storeId, page);
    }

}
