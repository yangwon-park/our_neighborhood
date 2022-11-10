package ywphsm.ourneighbor.config.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.util.UrlPathHelper;
import ywphsm.ourneighbor.config.ScriptUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        //스프링 시큐리티 로그인때 만든 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //현재 접속 url를 확인
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String originalURL = urlPathHelper.getOriginatingRequestUri(request);

        //로직을 짜서 상황에 따라 보내줄 주소를 설정해주면 됨
        ScriptUtils.alertAndBackPage(response, "접근권한이 없는 페이지 입니다.");
    }
}
