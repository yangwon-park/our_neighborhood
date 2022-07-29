package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class EditController {

    private final MemberService memberService;

    @GetMapping("/member_edit")
    public String memberEdit(@ModelAttribute EditForm editForm,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        editForm.setId(member.getId());
        editForm.setNickname(member.getNickname());
        editForm.setPhoneNumber(member.getPhoneNumber());

        return "edit/editForm";
    }

    @PostMapping("/member_edit")
    public String memberEdit(@Valid @ModelAttribute EditForm editForm, BindingResult bindingResult) {

        if (memberService.doubleCheck(editForm.getNickname()) != null) {
            bindingResult.reject("doubleCheck", new Object[]{editForm.getNickname()}, null);

            if (bindingResult.hasErrors()) {
                return "edit/editForm";
            }
        }

        memberService.update(editForm.getId(), editForm.getNickname(), editForm.getPhoneNumber());
        return "redirect:/";
    }

    @GetMapping("/member_edit/password_edit")
    public String passwordEdit(@ModelAttribute PasswordEditForm passwordEditForm) {
        return "edit/passwordEditForm";
    }

    @PostMapping("/member_edit/password_edit")
    public String passwordEdit(@Valid @ModelAttribute PasswordEditForm passwordEditForm,
                               BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (!memberService.passwordCheck(member.getPassword(), passwordEditForm.getBeforePassword())) {
            bindingResult.reject("passwordEqCheck");
        }

        if (!passwordEditForm.getAfterPassword().equals(passwordEditForm.getPasswordCheck())) {
            bindingResult.reject("passwordCheck");
        }

        if (bindingResult.hasErrors()) {
            return "/edit/passwordEditForm";
        }

        String encodedPassword = memberService.encodedPassword(passwordEditForm.getAfterPassword());

        memberService.updatePassword(member.getId(), encodedPassword);

        return "redirect:/logout";
    }
}
