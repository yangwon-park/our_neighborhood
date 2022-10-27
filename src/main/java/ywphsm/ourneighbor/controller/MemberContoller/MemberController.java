package ywphsm.ourneighbor.controller.MemberContoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ywphsm.ourneighbor.controller.form.LoginForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm) {
        return "member/loginForm";
    }

    @GetMapping("/sign_up")
    public String signUp(@ModelAttribute(value = "dto") MemberDTO.Add dto) {
        return "member/signUpForm";
    }
}
