package ywphsm.ourneighbor.controller.membercontoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.*;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user/member-edit")
public class EditController {

    @GetMapping
    public String memberEdit(@ModelAttribute(name = "editForm") EditForm editForm,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        editForm.setNickname(member.getNickname());
        editForm.setEmail(member.getEmail());
        editForm.setPhoneNumber(member.getPhoneNumber());

        return "member/edit/edit_form";
    }

    @GetMapping("/password")
    public String passwordEdit(@ModelAttribute PasswordEditForm passwordEditForm) {
        return "member/edit/password_edit_form";
    }

    @GetMapping("/phone-number")
    public String edit_phoneNumber(@ModelAttribute(name = "form") PhoneNumberEditForm phoneNumberEditForm) {
        return "member/edit/phone_edit_form";
    }

}
