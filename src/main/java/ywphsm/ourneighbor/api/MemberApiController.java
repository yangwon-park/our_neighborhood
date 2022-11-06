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
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.StoreService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.store.distance.Distance.calculateHowFarToTheTarget;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    private final StoreService storeService;

    @PutMapping("/user/like")
    public void likeAdd(boolean likeStatus, Long memberId, Long storeId) {
        log.info("likeStatus={}", likeStatus);
        storeService.updateLike(likeStatus, memberId, storeId);
    }

    @GetMapping("/member/check")
    public String check(String nickname, String email,
                        String phoneNumber, String certifiedNumber, String userId,
                        @SessionAttribute(name = SessionConst.PHONE_CERTIFIED, required = false)
                            PhoneCertifiedForm certifiedForm) {
        if (certifiedForm == null) {
            return "인증번호를 발송해 주세요.";
        }

        if (memberService.doubleCheck(nickname) != null) {
            return "이미 있는 닉네임 입니다.";
        }

        if (!phoneNumber.equals(certifiedForm.getPhoneNumber()) ||
                !certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return "전화번호 또는 인증번호가 일치하지 않습니다.";
        }

        if (memberService.findByPhoneNumber(phoneNumber) != null) {
            return "이미 있는 전화번호 입니다";
        }

        if (memberService.findByEmail(email) != null) {
            return "이미 있는 이메일 입니다";
        }

        if (memberService.userIdCheck(userId) != null) {
            return "이미 있는 아이디 입니다";
        }
        return "성공";
    }

    @GetMapping("/member/sendSMS")
    public boolean sendSMS(@RequestParam String phoneNumber, HttpServletRequest request) {

        if (memberService.findByPhoneNumber(phoneNumber) != null) {
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
    public Long save(MemberDTO.Add dto) {
        return memberService.save(dto);
    }

    @PutMapping("/member/edit/phoneNumber")
    public String updatePhoneNumber(String phoneNumber, String certifiedNumber,
                     @SessionAttribute(name = SessionConst.PHONE_CERTIFIED, required = false) PhoneCertifiedForm certifiedForm,
                     @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (certifiedForm == null) {
            return "인증번호를 발송해주세요.";
        }
        if (!phoneNumber.equals(certifiedForm.getPhoneNumber()) ||
                !certifiedNumber.equals(certifiedForm.getCertifiedNumber())) {
            return "전화번호 또는 인증번호가 일치하지 않습니다.";
        }

        memberService.updatePhoneNumber(member.getId(), phoneNumber);
        return "성공";
    }

    @PutMapping("/member/edit")
    public String update(EditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (memberService.doubleCheck(editForm.getNickname()) != null &&
                !member.getNickname().equals(editForm.getNickname())) {
            return "이미 존재하는 닉네임입니다.";
        }
        if (memberService.findByEmail(editForm.getEmail()) != null
                && !member.getEmail().equals(editForm.getEmail())) {
            return "이미 존재하는 이메일 입니다";
        }

        memberService.updateMember(member.getId(), editForm.getNickname(), editForm.getEmail());
        return "성공";
    }

    @PutMapping("/member/edit/password")
    public String updatePassword(PasswordEditForm editForm,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {

        if (!memberService.passwordCheck(member.getPassword(), editForm.getBeforePassword())) {
            return "기존 비밀번호가 일치하지 않습니다.";
        }

        if (!editForm.getAfterPassword().equals(editForm.getPasswordCheck())) {
            return "새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다";
        }

        String encodedPassword = memberService.encodedPassword(editForm.getAfterPassword());

        memberService.updatePassword(member.getId(), encodedPassword);
        return "성공";
    }

    @DeleteMapping("/member/withdrawal")
    public String delete(Long memberId) {

        memberService.withdrawal(memberId);
        return "redirect:/logout";
    }

    @PostMapping("/findUserId")
    public String findUserId(String email) {

        return memberService.sendEmailByUserId(email);
    }

    @PostMapping("/findPassword")
    public String findPassword(String email, String userId) {

        return memberService.sendEmailByPassword(email, userId);
    }

    @PutMapping("/admin/memberRole/edit")
    public String memberRoleEdit(String userId, Role role) {
        return memberService.updateRole(userId, role);
    }
}
