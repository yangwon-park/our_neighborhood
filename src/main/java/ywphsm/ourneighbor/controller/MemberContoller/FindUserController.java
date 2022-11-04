package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ywphsm.ourneighbor.controller.form.FindPasswordForm;
import ywphsm.ourneighbor.controller.form.FindUserIdForm;
import ywphsm.ourneighbor.service.MemberService;

@RequiredArgsConstructor
@Controller
public class FindUserController {

    private final MemberService memberService;

    @GetMapping("/find-userId")
    public String findId(@ModelAttribute(name = "findUserIdForm") FindUserIdForm findUserIdForm) {
        return "member/find_userid";
    }

    @GetMapping("/find-password")
    public String findPassword(@ModelAttribute(name = "findPasswordForm") FindPasswordForm findPasswordForm) {
        return "member/find_password";
    }
}