package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.HashtagService;
import ywphsm.ourneighbor.service.ReviewService;
import ywphsm.ourneighbor.service.StoreService;

import java.io.IOException;

import static ywphsm.ourneighbor.domain.hashtag.HashtagOfStore.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewApiController {

    private final ReviewService reviewService;

    private final HashtagService hashtagService;

    private final StoreService storeService;

    // hastag는 Store에 연관돼있음
    @PostMapping("/user/review")
    public Long save(ReviewDTO.Add dto, @RequestParam String hashtag) throws IOException, ParseException {
        if (!hashtag.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(hashtag);

            Store findStore = storeService.findById(dto.getStoreId());

            for (Object object : array) {
                JSONObject jsonObject = (JSONObject)object;

                HashtagDTO hashtagDTO = HashtagDTO.builder()
                        .name(jsonObject.get("value").toString())
                        .build();

                boolean duplicateCheck = hashtagService.checkHashtagDuplicate(hashtagDTO.getName());

                Hashtag savedHashtag;

                if (!duplicateCheck) {
                    savedHashtag = hashtagService.save(hashtagDTO);
                } else {
                    savedHashtag = hashtagService.findByName(hashtagDTO.getName());
                }

                linkHashtagAndStore(savedHashtag, findStore);
            }
        }

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
