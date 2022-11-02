package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.*;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user/member_edit")
public class EditController {

    @GetMapping
    public String memberEdit(@ModelAttribute(name = "editForm") EditForm editForm,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        editForm.setNickname(member.getNickname());
        editForm.setEmail(member.getEmail());
        editForm.setPhoneNumber(member.getPhoneNumber());

        return "member/edit/editForm";
    }

    @GetMapping("/password")
    public String passwordEdit(@ModelAttribute PasswordEditForm passwordEditForm) {
        return "member/edit/passwordEditForm";
    }

    @GetMapping("/phoneNumber")
    public String edit_phoneNumber(@ModelAttribute(name = "form") PhoneNumberEditForm phoneNumberEditForm) {
        return "member/edit/phoneEditForm";
    }

}
