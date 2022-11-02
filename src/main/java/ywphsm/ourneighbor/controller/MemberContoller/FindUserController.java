package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ywphsm.ourneighbor.controller.form.FindPasswordForm;
import ywphsm.ourneighbor.controller.form.FindUserIdForm;
import ywphsm.ourneighbor.service.MemberService;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class FindUserController {

    private final MemberService memberService;

    @GetMapping("/findUserId")
    public String findId(@ModelAttribute(name = "findUserIdForm") FindUserIdForm findUserIdForm) {
        return "member/findUserId";
    }

    @GetMapping("/findPassword")
    public String findPassword(@ModelAttribute(name = "findPasswordForm") FindPasswordForm findPasswordForm) {
        return "member/findPassword";
    }
}