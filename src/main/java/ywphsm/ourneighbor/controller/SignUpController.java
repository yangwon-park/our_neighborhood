package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.MemberForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.email.TokenService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sign_up")
public class SignUpController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @GetMapping
    public String signUp(@ModelAttribute MemberForm memberForm) {
        return "sign/signUpForm";
    }

    @PostMapping
    public String signUp(@Valid @ModelAttribute MemberForm memberForm,
                         BindingResult bindingResult) {

        if (!memberForm.getPassword().equals(memberForm.getPasswordCheck())) {
            bindingResult.reject("passwordCheck");
        }

        if (memberService.doubleCheck(memberForm.getUsername()) != null) {
            bindingResult.reject("doubleCheck", new Object[]{memberForm.getNickname()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "sign/signUpForm";
        }

        //패스워드 암호화
        String encodedPassword = memberService.encodedPassword(memberForm.getPassword());

        Member member = new Member(memberForm.getUsername(), memberForm.getAge(), memberForm.getPhoneNumber(),
                memberForm.getGender(), memberForm.getUserId(), encodedPassword,
                memberForm.getEmail(), memberForm.getNickname());

        memberService.join(member);
        tokenService.createEmailToken(member.getId(), member.getEmail());
        return "redirect:/ ";
    }

    @GetMapping("/confirm-email")
    public String viewConfirmEmail(@RequestParam String token) {
        memberService.confirmEmail(token);

        return "redirect:/login";
    }
}
