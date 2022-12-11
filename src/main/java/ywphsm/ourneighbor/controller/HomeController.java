package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CategoryService categoryService;

    private final StoreService storeService;

    @GetMapping("/")
    public String index(Model model) {
        final long depth = 1L;

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepthCaseByOrderByName(depth);
        model.addAttribute("rootCategoryList", rootCategoryList);

        return "index";
    }

    @GetMapping("/map")
    public String map(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "map";
    }
}