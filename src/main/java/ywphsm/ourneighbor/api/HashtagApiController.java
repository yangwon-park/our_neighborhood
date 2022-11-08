package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.service.HashtagOfMenuService;
import ywphsm.ourneighbor.service.HashtagOfStoreService;
import ywphsm.ourneighbor.service.HashtagService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HashtagApiController {

    private final HashtagService hashtagService;

    private final HashtagOfStoreService hashtagOfStoreService;

    private final HashtagOfMenuService hashtagOfMenuService;

    @GetMapping("/hashtags/all")
    public ResultClass<?> findAllHashtags() {
        List<HashtagDTO> hashtags = hashtagService.findAll();
        return new ResultClass<>(hashtags);
    }

    @GetMapping("/hashtags")
    public ResultClass<?> findHashtagsByMenuId(@RequestParam Long menuId) {
        List<HashtagDTO> hashtags = hashtagOfMenuService.findHashtagsByMenuId(menuId);
        return new ResultClass<>(hashtags);
    }

    @PostMapping("/seller/hashtag/{storeId}")
    public Long saveHashtag(@PathVariable Long storeId, HashtagDTO dto) {
        return hashtagService.simpleSaveLinkedStore(storeId, dto);
    }

    @DeleteMapping("/seller/hashtag/{hashtagId}")
    public Long deleteHashtagOfStore(@PathVariable Long hashtagId, @RequestParam Long storeId) {
        return hashtagOfStoreService.deleteHashtagOfStore(hashtagId, storeId);
    }
}
