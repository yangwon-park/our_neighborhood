package ywphsm.ourneighbor.controller;

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
    public String findId(@ModelAttribute FindUserIdForm findUserIdForm) {
        return "login/findUserId";
    }

    @PostMapping("/findUserId")
    public String findId(@Valid @ModelAttribute FindUserIdForm findUserIdForm,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "login/findUserId";
        }

        memberService.findUserId(findUserIdForm.getEmail());

        return "redirect:/login";
    }

    @GetMapping("/findPassword")
    public String findPassword(@ModelAttribute FindPasswordForm findPasswordForm) {
        return "login/findPassword";
    }

    @PostMapping("/findPassword")
    public String passwordEdit(@Valid @ModelAttribute FindPasswordForm findPasswordForm,
                               BindingResult bindingResult) {

        if (!findPasswordForm.getNewPassword().equals(findPasswordForm.getPasswordCheck())) {
            bindingResult.reject("passwordCheck");
        }

        if (memberService.userIdCheck(findPasswordForm.getUserId()) == null) {
            bindingResult.reject("userIdCheck");
        }

        if (bindingResult.hasErrors()) {
            return "login/findPassword";
        }

        String encodedPassword = memberService.encodedPassword(findPasswordForm.getNewPassword());

        memberService.updatePassword(findPasswordForm.getUserId(), encodedPassword);

        return "redirect:/login";
    }
}
