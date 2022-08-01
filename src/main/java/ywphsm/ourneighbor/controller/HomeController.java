package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final MemberService memberService;
    private final StoreService storeService;

    // 검색 뷰페이지 임시
    @GetMapping("/prac")
    public String prac(Model model, @ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        List<Store> stores = storeService.searchByKeyword(storeSearchCond);
        model.addAttribute("stores", stores);

        return "prac";
    }

    @GetMapping("/")
    public String home(Model model, @ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        List<Store> stores = storeService.searchByKeyword(storeSearchCond);
        model.addAttribute("stores", stores);

        return "index";
    }

}
