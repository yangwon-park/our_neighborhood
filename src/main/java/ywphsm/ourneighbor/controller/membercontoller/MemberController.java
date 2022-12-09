package ywphsm.ourneighbor.controller.membercontoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.controller.form.LoginForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/login")
    public String login(@ModelAttribute(name = "loginForm") LoginForm loginForm,
                        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {

        if (member != null) {
            ScriptUtils.alertAndMovePage(response, "이미 로그인이 되어있습니다.", "/");
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            ScriptUtils.alert(response, "로그인후 이용해 주세요.");
        }

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "member/login_form";
    }

    @GetMapping("/sign-up")
    public String signUp(@ModelAttribute(value = "dto") MemberDTO.Add dto) {
        return "member/sign_up_form";
    }

    @GetMapping("/admin/member-role/edit")
    public String memberRoleEdit() {
        return "member/edit/member_role_edit";
    }

    @GetMapping("/delete-request-cache")
    public void deleteRequestCache(HttpServletResponse response, HttpServletRequest request) {
        requestCache.removeRequest(request, response);
    }

}
