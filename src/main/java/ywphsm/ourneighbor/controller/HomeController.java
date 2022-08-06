package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.SessionAttribute;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;
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


    // 검색 뷰페이지 임시
    @GetMapping("/prac2")
    public String prac2(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "prac2";
    }

    // 검색 뷰페이지 임시
    @GetMapping("/prac3")
    public String prac3(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "prac3";
    }

    @GetMapping("/loginHome")
    public String loginhome(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            Model model) {

        if (member == null) {
            return "login";
        }

        model.addAttribute("member", member);

        return "loginHome";
    }
    

    @GetMapping("/")
    public String home(Model model, @ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        List<Store> stores = storeService.searchByKeyword(storeSearchCond);
        model.addAttribute("stores", stores);

        return "index";

    }

}
