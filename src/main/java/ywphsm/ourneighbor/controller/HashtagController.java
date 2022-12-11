package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.service.HashtagOfStoreService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HashtagController {

    private final HashtagOfStoreService hashtagOfStoreService;

    @GetMapping("/seller/hashtag/{storeId}")
    public String getHashtag(@PathVariable Long storeId, Model model) {
        List<HashtagOfStoreDTO.WithCount> hashtagList =
                hashtagOfStoreService.findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(storeId);

        model.addAttribute("hashtagList", hashtagList);
        model.addAttribute("storeId", storeId);

        return "hashtag/list";
    }
}
