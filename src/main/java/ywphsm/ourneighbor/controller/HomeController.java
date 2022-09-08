package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.service.StoreService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    // 검색 뷰페이지 임시
    @GetMapping("/map")
    public String map(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "map";
    }

    @GetMapping("/prac2")
    public String addStore(Model model) {
        List<CategoryDTO> all = categoryService.findAll();

        log.info("all={}", all);

        model.addAttribute("all", all);
        model.addAttribute("store", new StoreDTO.Add());

        return "prac2";
    }

    @PostMapping("/prac2")
    public String addStore(@ModelAttribute StoreDTO.Add storeAddDTO, @RequestParam Long categoryId) {
        log.info("storeAddDTO={}", storeAddDTO);
        log.info("categoryId={}", categoryId);

        Category category = categoryService.findById(categoryId);
        log.info("category={}", category.getCategoryOfStoreList());

        storeService.saveStore(storeAddDTO, category);

        return "redirect:/prac2";
    }

    @GetMapping("/")
    public String index(HttpServletRequest req) {

        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("cookie = " + cookie.getName());
        }

        return "index";
    }

}