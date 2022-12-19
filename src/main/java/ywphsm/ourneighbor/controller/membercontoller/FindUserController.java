package ywphsm.ourneighbor.controller.membercontoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ywphsm.ourneighbor.controller.form.FindPasswordForm;
import ywphsm.ourneighbor.controller.form.FindUserIdForm;

@RequiredArgsConstructor
@Controller
public class FindUserController {

    @GetMapping("/find-userId")
    public String findId(@ModelAttribute(name = "findUserIdForm") FindUserIdForm findUserIdForm) {
        return "member/find_userid";
    }

    @GetMapping("/find-password")
    public String findPassword(@ModelAttribute(name = "findPasswordForm") FindPasswordForm findPasswordForm) {
        return "member/find_password";
    }
}