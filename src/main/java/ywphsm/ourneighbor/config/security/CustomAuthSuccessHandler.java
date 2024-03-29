package ywphsm.ourneighbor.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.email.security.MemberDetailsImpl;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        clearSession(request);

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        /*
            prevPage가 존재하는 경우 = 사용자가 직접 /login 경로로 로그인 요청
            기존 Session의 prevPage attribute 제거
         */
        String prevPage = (String) request.getSession().getAttribute("prevPage");
        if (prevPage != null) {
            request.getSession().removeAttribute("prevPage");
        }

        String uri = "/";       // 기본 URI

        /*
            savedRequest 존재하는 경우 = 인증 권한이 없는 페이지 접근
            Security Filter가 인터셉트하여 savedRequest에 세션 저장
         */
        if (savedRequest != null) {
            uri = savedRequest.getRedirectUrl();
        } else if (prevPage != null && !prevPage.equals("")) {
            /*
                회원가입 - 로그인으로 넘어온 경우 "/"로 redirect
             */
            if (prevPage.contains("/sign_up")) {
                uri = "/";
            } else {
                uri = prevPage;
            }
        }

        authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetailsImpl principal = (MemberDetailsImpl) authentication.getPrincipal();
        Member loginMember = principal.getMember();

        request.getSession().setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        redirectStrategy.sendRedirect(request, response, uri);
    }

    /*
        로그인 실패 후 성공 시 남아있는 에러 세션 제거
     */
    protected void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}