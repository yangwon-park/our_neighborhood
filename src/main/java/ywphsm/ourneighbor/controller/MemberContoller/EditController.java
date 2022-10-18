package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.*;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.email.TokenService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user/member_edit")
public class EditController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @GetMapping
    public String memberEdit(@ModelAttribute EditForm editForm,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        editForm.setId(member.getId());
        editForm.setNickname(member.getNickname());

        return "edit/editForm";
    }

    @PostMapping
    public String memberEdit(@Valid @ModelAttribute EditForm editForm, BindingResult bindingResult) {

        Member member = memberService.findById(editForm.getId());

        if (memberService.doubleCheck(editForm.getNickname()) != null &&
                !member.getNickname().equals(editForm.getNickname())) {
            bindingResult.reject("doubleCheck", new Object[]{editForm.getNickname()}, null);

            if (bindingResult.hasErrors()) {
                return "edit/editForm";
            }
        }

        memberService.updateNickname(editForm.getId(), editForm.getNickname());
        return "redirect:/user/myPage";
    }

    @GetMapping("/password_edit")
    public String passwordEdit(@ModelAttribute PasswordEditForm passwordEditForm) {
        return "edit/passwordEditForm";
    }

    @PostMapping("/password_edit")
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

    @GetMapping("/confirm_email")
    public String confirmEmail(@ModelAttribute EmailConfirmForm emailConfirmForm,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (member.isEmailConfirm()) {
            return "edit/alreadyEmailConfirm";
        }

        return "edit/EmailConfirmForm";
    }

    @PostMapping("/confirm_email")
    public String confirmEmail(@Valid @ModelAttribute EmailConfirmForm emailConfirmForm,
                               BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (memberService.findByEmail(emailConfirmForm.getEmail()) != null) {
            bindingResult.reject("emailDoubleCheck");
        }

        if (bindingResult.hasErrors()) {
            return "edit/EmailConfirmForm";
        }

        tokenService.createEmailToken(member.getId(), emailConfirmForm.getEmail());

        return "signUp/confirmEmail";
    }

    @GetMapping("/phoneCertified")
    public String edit_phoneCertified(@ModelAttribute PhoneNumberEditForm phoneNumberEditForm,
                                      @SessionAttribute(name = SessionConst.PHONE_CERTIFIED) PhoneCertifiedForm phoneSession) {

        phoneNumberEditForm.setPhoneNumber(phoneSession.getPhoneNumber());
        return "edit/phoneCertifiedForm";
    }

    @PostMapping("/phoneCertified")
    public String edit_phoneCertified(@Valid @ModelAttribute PhoneNumberEditForm phoneNumberEditForm,
                                      BindingResult bindingResult,
                                      @SessionAttribute(name = SessionConst.PHONE_CERTIFIED) PhoneCertifiedForm phoneSession,
                                      @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (!phoneNumberEditForm.getPhoneNumber().equals(phoneSession.getPhoneNumber()) ||
                !phoneNumberEditForm.getCertifiedNumber().equals(phoneSession.getCertifiedNumber())) {
            bindingResult.reject("phoneCertifiedFail");
        }

        if (memberService.findByPhoneNumber(phoneNumberEditForm.getPhoneNumber()) != null &&
        !member.getPhoneNumber().equals(phoneNumberEditForm.getPhoneNumber())) {
            bindingResult.reject("phoneDoubleCheck");
        }

        if (bindingResult.hasErrors()) {
            return "edit/phoneCertifiedForm";
        }

        memberService.updatePhoneNumber(member.getId(), phoneNumberEditForm.getPhoneNumber());


        return "redirect:/logout";
    }

    //회원탈퇴
    @GetMapping("/withdrawal")
    public String withdrawal(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        memberService.withdrawal(member.getId());
        return "redirect:/logout";
    }
}
