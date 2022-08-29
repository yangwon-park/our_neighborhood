package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.EmailConfirmForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.email.TokenService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/member_edit")
@Controller
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
    public String memberEdit(@Valid @ModelAttribute EditForm editForm, BindingResult bindingResult,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (memberService.doubleCheck(editForm.getNickname()) != null &&
                !member.getNickname().equals(editForm.getNickname())) {
            bindingResult.reject("doubleCheck", new Object[]{editForm.getNickname()}, null);

            if (bindingResult.hasErrors()) {
                return "edit/editForm";
            }
        }

        memberService.updateNickname(editForm.getId(), editForm.getNickname());
        return "redirect:/";
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
            return "edit/alreadyEmailConfirm"; // 이미 인증된 이메일입니다 메세지 띄워야함
        }

        return "edit/EmailConfirmForm";
    }

    @PostMapping("/confirm_email")
    public String confirmEmail(@Valid @ModelAttribute EmailConfirmForm emailConfirmForm,
                               BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (bindingResult.hasErrors()) {
            return "edit/EmailConfirmForm";
        }

        tokenService.createEmailToken(member.getId(), emailConfirmForm.getEmail());

        return "signUp/confirmEmail";
    }

    @GetMapping("/phoneCertified")
    public String edit_phoneCertified(@ModelAttribute PhoneCertifiedForm phoneCertifiedForm,
                                      @SessionAttribute(name = SessionConst.PHONE_CERTIFIED) PhoneCertifiedForm phoneSession) {

        phoneSession.setPhoneNumber(phoneSession.getPhoneNumber());
        return "edit/phoneCertifiedForm";
    }

    @PostMapping("/phoneCertified")
    public String edit_phoneCertified(@Valid @ModelAttribute PhoneCertifiedForm phoneCertifiedForm,
                                      BindingResult bindingResult,
                                      @SessionAttribute(name = SessionConst.PHONE_CERTIFIED) PhoneCertifiedForm phoneSession,
                                      @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (!phoneCertifiedForm.getPhoneNumber().equals(phoneSession.getPhoneNumber()) ||
                !phoneCertifiedForm.getCertifiedNumber().equals(phoneSession.getCertifiedNumber())) {
            bindingResult.reject("phoneCertifiedFail");
        }

        if (memberService.findByPhoneNumber(phoneCertifiedForm.getPhoneNumber()) != null) {
            bindingResult.reject("phoneDoubleCheck");
        }

        if (bindingResult.hasErrors()) {
            return "edit/phoneCertifiedForm";
        }

        memberService.updatePhoneNumber(member.getId(), phoneCertifiedForm.getPhoneNumber());

        return "redirect:/logout";
    }

    //회원탈퇴
    @GetMapping("/withdrawal")
    public String withdrawal(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        memberService.withdrawal(member.getId());
        return "redirect:/logout";
    }
}
