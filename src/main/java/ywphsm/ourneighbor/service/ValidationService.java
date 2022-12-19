package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.member.Member;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final MemberService memberService;

    public String memberSaveValid(String nickname, String email, String phoneNumber, String certifiedNumber, String userId, PhoneCertifiedForm certifiedForm) {

        if (certifiedForm == null) {
            return "인증번호를 발송해 주세요.";
        }

        if (memberService.findByNickName(nickname).isPresent()) {
            return "이미 있는 닉네임 입니다.";
        }

        if (!phoneNumber.equals(certifiedForm.getPhoneNumber()) ||
                !certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return "전화번호 또는 인증번호가 일치하지 않습니다.";
        }

        if (memberService.findByPhoneNumber(phoneNumber).isPresent()) {
            return "이미 있는 전화번호 입니다";
        }

        if (memberService.findByEmail(email).isPresent()) {
            return "이미 있는 이메일 입니다";
        }

        if (memberService.findByUserId(userId).isPresent()) {
            return "이미 있는 아이디 입니다";
        }
        return "성공";
    }


    public String memberUpdatePnValid(String phoneNumber, String certifiedNumber, PhoneCertifiedForm certifiedForm, Member member) {

        if (certifiedForm == null) {
            return "인증번호를 발송해주세요.";
        }
        if (!phoneNumber.equals(certifiedForm.getPhoneNumber()) ||
                !certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return "전화번호 또는 인증번호가 일치하지 않습니다.";
        }

        return "성공";
    }

    public String memberUpdateValid(EditForm editForm, Member member) {

        if (memberService.findByNickName(editForm.getNickname()).isPresent() &&
                !member.getNickname().equals(editForm.getNickname())) {
            return "이미 존재하는 닉네임입니다.";
        }

        if (memberService.findByEmail(editForm.getEmail()).isPresent()
                && !member.getEmail().equals(editForm.getEmail())) {
            return "이미 존재하는 이메일 입니다";
        }

        return "성공";
    }

    public String memberUpdatePwValid(PasswordEditForm editForm, Member member) {

        if (!memberService.passwordCheck(member.getPassword(), editForm.getBeforePassword())) {
            return "기존 비밀번호가 일치하지 않습니다.";
        }

        if (!editForm.getAfterPassword().equals(editForm.getPasswordCheck())) {
            return "새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다";
        }

        return "성공";
    }

}
