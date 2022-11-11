package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.service.CategoryService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model) {
        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepth(1L);

        model.addAttribute("rootCategoryList", rootCategoryList);

        return "index";
    }

    // 검색 뷰페이지 임시
    @GetMapping("/map")
    public String map(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "map";
    }
}