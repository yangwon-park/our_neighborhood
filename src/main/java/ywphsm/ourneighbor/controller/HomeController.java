package ywphsm.ourneighbor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            Model model) {

        if (member == null) {
            return "login";
        }

        model.addAttribute("member", member);

        return "loginHome";
    }
}
