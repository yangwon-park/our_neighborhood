package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ywphsm.ourneighbor.service.KakaoService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/sign_in/kakao")
    public String kakaoIndex(@RequestParam String code, HttpServletRequest request) {
        log.info("code={}", code);

        String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
        kakaoService.createKakaoUser(kakaoAccessToken);

        HttpSession session = request.getSession();
//        session.setAttribute(SessionConst.LOGIN_MEMBER, kakaoUserId);

        return "home";
    }

}
