package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ywphsm.ourneighbor.domain.dto.HashtagDTO;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HashtagController {

    @GetMapping("/seller/hashtag/{storeId}")
    public String getHashtag(@PathVariable Long storeId, Model model) {
        model.addAttribute("hashtag", new HashtagDTO());
        return "hashtag/form";
    }
}
