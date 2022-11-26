package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecommendPostController {

    @GetMapping("/admin/recommend-post/add")
    public String addRecommendPost(Model model) {
        model.addAttribute("recommend", new RecommendPostDTO.Add());
        model.addAttribute("recommendKinds", RecommendKind.values());
        return "recommend_post/add_form";
    }
}
