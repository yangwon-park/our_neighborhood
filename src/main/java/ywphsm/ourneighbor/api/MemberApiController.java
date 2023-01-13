package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.controller.form.PasswordEditForm;
import ywphsm.ourneighbor.controller.form.PhoneCertifiedForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;

import ywphsm.ourneighbor.domain.member.Role;

import ywphsm.ourneighbor.service.member.MemberReviewService;
import ywphsm.ourneighbor.service.member.MemberService;
import ywphsm.ourneighbor.service.validation.ValidationConst;
import ywphsm.ourneighbor.service.validation.ValidationService;
import ywphsm.ourneighbor.service.member.SmsService;
import ywphsm.ourneighbor.service.member.login.SessionConst;
import ywphsm.ourneighbor.service.store.StoreService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;
    private final MemberReviewService memberReviewService;

    private final StoreService storeService;

    private final ValidationService validationService;

    private final SmsService smsService;

    @PutMapping("/user/like")
    public boolean likeAdd(boolean likeStatus, Long memberId, Long storeId) {
        log.info("likeStatus={}", likeStatus);
        return storeService.updateLike(likeStatus, memberId, storeId);
    }

    @GetMapping("/member/check")
    public String check(String nickname, String email,
                        String phoneNumber, String certifiedNumber, String userId,
                        @SessionAttribute(name = SessionConst.PHONE_CERTIFIED, required = false)
                            PhoneCertifiedForm certifiedForm) {

        return validationService.memberSaveValid(nickname, email, phoneNumber, certifiedNumber,
                userId, certifiedForm);
    }

    @GetMapping("/member/send-sms")
    public boolean sendSMS(@RequestParam String phoneNumber, HttpServletRequest request) {

        if (memberService.findByPhoneNumber(phoneNumber).isPresent()) {
            return false;
        }

        smsService.certifiedPhoneNumber(phoneNumber, request);
        return true;
    }

    @PostMapping("/member/add")
    public Long save(@Valid MemberDTO.Add dto) throws IOException {
        return memberService.save(dto);
    }

    @PutMapping("/member/edit/phone-number")
    public String updatePhoneNumber(String phoneNumber, String certifiedNumber,
                     @SessionAttribute(name = SessionConst.PHONE_CERTIFIED, required = false) PhoneCertifiedForm certifiedForm,
                     @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        String valid = validationService.memberUpdatePnValid(phoneNumber, certifiedNumber, certifiedForm, member);

        if (!valid.equals(ValidationConst.SUCCES)) {
            return valid;
        }

        memberService.updatePhoneNumber(member.getId(), phoneNumber);
        return valid;
    }

    @PutMapping("/member/edit")
    public String update(EditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) throws IOException {

        String valid = validationService.memberUpdateValid(editForm, member);

        if (!valid.equals(ValidationConst.SUCCES)) {
            return valid;
        }

        memberService.updateMember(member.getId(), editForm);
        return valid;
    }

    @PutMapping("/member/edit/password")
    public String updatePassword(PasswordEditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        String valid = validationService.memberUpdatePwValid(editForm, member);

        if (valid.equals(ValidationConst.SUCCES)) {
            String encodedPassword = memberService.encodedPassword(editForm.getAfterPassword());
            memberService.updatePassword(member.getId(), encodedPassword);
            return valid;
        }

        return valid;
    }

    @DeleteMapping("/member/withdrawal")
    public void delete(Long memberId) {
        memberReviewService.withdrawal(memberId);
    }

    @PostMapping("/find-userid")
    public String findUserId(String email) {

        String valid = validationService.findUserIdValid(email);

        if (valid.equals(ValidationConst.SUCCES)) {
            memberService.sendEmailByUserId(email);
            return valid;
        }

        return valid;
    }

    @PostMapping("/find-password")
    public String findPassword(String email, String userId) {

        String valid = validationService.findPasswordValid(email, userId);

        if (valid.equals(ValidationConst.SUCCES)) {
            memberService.sendEmailByPassword(email);
            return valid;
        }

        return valid;
    }

    @PutMapping("/admin/member-role/edit")
    public String memberRoleEdit(String userId, Role role) {
        return memberService.updateRole(userId, role);
    }

    @GetMapping("/delete-certified-number")
    public void deleteCertifiedNumber(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(SessionConst.PHONE_CERTIFIED);
    }

    @DeleteMapping("/admin/withdrawal")
    public boolean delete(String userId) {
        return memberReviewService.adminWithdrawal(userId);
    }

    @PostMapping("/member/api-add")
    public String apiSave(@Valid MemberDTO.ApiAdd dto,HttpServletRequest request,
                          @SessionAttribute(name = SessionConst.API_MEMBER, required = false) Member member) {
        String valid = validationService.memberNicknameValid(dto.getNickname());

        if (valid.equals(ValidationConst.SUCCES)) {
            memberService.apiSave(dto, member.getFile());
            request.getSession().removeAttribute(SessionConst.API_MEMBER);
            return valid;
        }
        return valid;
    }
}
