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
import ywphsm.ourneighbor.service.ValidationService;
import ywphsm.ourneighbor.service.member.login.SessionConst;
import ywphsm.ourneighbor.service.store.StoreService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.IOException;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;
    private final MemberReviewService memberReviewService;

    private final StoreService storeService;

    private final ValidationService validationService;



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

        Random rand = new Random();
        String certifiedNumber = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            certifiedNumber += ran;
        }

        log.info("수신자 번호:{}", phoneNumber);
        log.info("인증 번호:{}", certifiedNumber);
        memberService.certifiedPhoneNumber(phoneNumber, certifiedNumber);

        PhoneCertifiedForm certifiedForm = new PhoneCertifiedForm();
        certifiedForm.setPhoneNumber(phoneNumber);
        certifiedForm.setCertifiedNumber(certifiedNumber);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.PHONE_CERTIFIED, certifiedForm);

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

        if (!valid.equals("성공")) {
            return valid;
        }

        memberService.updatePhoneNumber(member.getId(), phoneNumber);
        return valid;
    }

    @PutMapping("/member/edit")
    public String update(EditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) throws IOException {

        String valid = validationService.memberUpdateValid(editForm, member);

        if (!valid.equals("성공")) {
            return valid;
        }

        memberService.updateMember(member.getId(), editForm);
        return valid;
    }

    @PutMapping("/member/edit/password")
    public String updatePassword(PasswordEditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        String valid = validationService.memberUpdatePwValid(editForm, member);

        if (!valid.equals("성공")) {
            return valid;
        }

        String encodedPassword = memberService.encodedPassword(editForm.getAfterPassword());
        memberService.updatePassword(member.getId(), encodedPassword);

        return valid;
    }

    @DeleteMapping("/member/withdrawal")
    public void delete(Long memberId) {
        memberReviewService.withdrawal(memberId);
    }

    @PostMapping("/find-userid")
    public String findUserId(String email) {

        return memberService.sendEmailByUserId(email);
    }

    @PostMapping("/find-password")
    public String findPassword(String email, String userId) {

        return memberService.sendEmailByPassword(email, userId);
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
}
