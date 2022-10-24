package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.service.StoreService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 검색 뷰페이지 임시
    @GetMapping("/map")
    public String map(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "map";
    }
}