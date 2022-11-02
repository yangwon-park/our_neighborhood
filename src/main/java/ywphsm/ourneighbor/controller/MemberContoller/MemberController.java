package ywphsm.ourneighbor.controller.MemberContoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import ywphsm.ourneighbor.controller.form.LoginForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String login(@ModelAttribute(name = "loginForm") LoginForm loginForm,
                        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model) {

        if (member != null) {
            return "redirect:/user/myPage";
        }

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "member/loginForm";
    }

    @GetMapping("/sign_up")
    public String signUp(@ModelAttribute(value = "dto") MemberDTO.Add dto) {
        return "member/signUpForm";
    }
}
