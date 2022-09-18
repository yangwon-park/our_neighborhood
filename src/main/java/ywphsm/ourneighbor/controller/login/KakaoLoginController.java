package ywphsm.ourneighbor.controller.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.KakaoService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Slf4j
@Controller
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/login/kakaoUrl")
    public String googleUrl() {
        return "redirect:https://kauth.kakao.com/oauth/authorize?client_id=cd429446ad94ee1e20c77038ad37b1a2&redirect_uri=http://localhost:8080/login/kakao&response_type=code";
    }

    @GetMapping("/login/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletRequest request) throws JsonProcessingException {
        log.info("code={}", code);

        Member kakaoUser = kakaoService.getKakaoAccessToken(code);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, kakaoUser);

        return "redirect:/";
    }

}
