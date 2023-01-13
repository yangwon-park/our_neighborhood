package ywphsm.ourneighbor.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.member.MemberService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final MemberService memberService;

    public String memberSaveValid(String nickname, String email, String phoneNumber, String certifiedNumber, String userId, PhoneCertifiedForm certifiedForm) {

        if (certifiedForm == null) {
            return ValidationConst.CERTIFIED_NULL;
        }

        if (memberService.findByNickName(nickname).isPresent()) {
            return ValidationConst.NICKNAME_DUPLICATE;
        }

        if (!certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return ValidationConst.CERTIFIEDNUMBER_MISMATCH;
        }

        if (!phoneNumber.equals(certifiedForm.getPhoneNumber())) {
            return ValidationConst.PHONENUMBER_MISMATCH;
        }

        if (memberService.findByPhoneNumber(phoneNumber).isPresent()) {
            return ValidationConst.PHONENUMBER_DUPLICATE;
        }

        if (memberService.findByEmail(email).isPresent()) {
            return ValidationConst.EMAIL_DUPLICATE;
        }

        if (memberService.findByUserId(userId).isPresent()) {
            return ValidationConst.USERID_DUPLICATE;
        }
        return ValidationConst.SUCCES;
    }


    public String memberUpdatePnValid(String phoneNumber, String certifiedNumber, PhoneCertifiedForm certifiedForm, Member member) {

        if (certifiedForm == null) {
            return ValidationConst.CERTIFIED_NULL;
        }
        if (!certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return ValidationConst.CERTIFIEDNUMBER_MISMATCH;
        }

        if (!phoneNumber.equals(certifiedForm.getPhoneNumber())) {
            return ValidationConst.PHONENUMBER_MISMATCH;
        }

        return ValidationConst.SUCCES;
    }

    public String memberUpdateValid(EditForm editForm, Member member) {

        if (memberService.findByNickName(editForm.getNickname()).isPresent() &&
                !member.getNickname().equals(editForm.getNickname())) {
            return ValidationConst.NICKNAME_DUPLICATE;
        }

        if (memberService.findByEmail(editForm.getEmail()).isPresent()
                && !member.getEmail().equals(editForm.getEmail())) {
            return ValidationConst.EMAIL_DUPLICATE;
        }

        return ValidationConst.SUCCES;
    }

    public String memberUpdatePwValid(PasswordEditForm editForm, Member member) {

        if (!memberService.passwordCheck(member.getPassword(), editForm.getBeforePassword())) {
            return ValidationConst.PASSWORD_MISMATCH;
        }

        if (!editForm.getAfterPassword().equals(editForm.getPasswordCheck())) {
            return ValidationConst.PASSWORD_CONFIRMATION_MISMATCH;
        }

        return ValidationConst.SUCCES;
    }

    public String memberNicknameValid(String nickname) {
        if (memberService.findByNickName(nickname).isPresent()) {
            return ValidationConst.NICKNAME_DUPLICATE;
        }

        return ValidationConst.SUCCES;
    }

    public String findUserIdValid(String email) {
        Optional<Member> member = memberService.findByEmail(email);

        if (member.isEmpty()) {
            return ValidationConst.EMAIL_NULL;
        }

        if (member.get().getUserId() == null) {
            return ValidationConst.USERID_NULL_API;
        }

        return ValidationConst.SUCCES;
    }

    public String findPasswordValid(String email, String userId) {
        Optional<Member> member = memberService.findByEmail(email);

        if (member.isEmpty()) {
            return ValidationConst.EMAIL_NULL;
        }

        if (!member.get().getUserId().equals(userId)) {
            return ValidationConst.USERID_MISMATCH;
        }

        return ValidationConst.SUCCES;
    }
}
