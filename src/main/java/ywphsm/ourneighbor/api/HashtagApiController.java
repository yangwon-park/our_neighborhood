package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.HashtagDTO;
import ywphsm.ourneighbor.service.HashtagOfStoreService;
import ywphsm.ourneighbor.service.HashtagService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HashtagApiController {

    private final HashtagService hashtagService;

    private final HashtagOfStoreService hashtagOfStoreService;

    @GetMapping("/hashtags")
    public ResultClass<?> findAllHashtags() {
        List<HashtagDTO> hashtags = hashtagService.findAll();
        return new ResultClass<>(hashtags);
    }

    @DeleteMapping("/seller/hashtag/{hashtagId}")
    public Long delete(@PathVariable Long hashtagId, @RequestParam Long storeId) {
        return hashtagOfStoreService.delete(hashtagId, storeId);
    }
}
