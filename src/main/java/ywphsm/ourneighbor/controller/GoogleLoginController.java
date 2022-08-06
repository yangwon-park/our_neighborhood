package ywphsm.ourneighbor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.GoogleService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleService googleService;

    @Value("${google.scope}")
    private String GOOGLE_SCOPE;

    @GetMapping("/login/googleUrl")
    public String googleUrl() {
        return "redirect:https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?scope=" + GOOGLE_SCOPE + "&response_type=code&client_id=1008060436788-dmq8hj575pafbfnajpmdkouao0lm2ucg.apps.googleusercontent.com&redirect_uri=http://localhost:8080/login/google";
    }

    @GetMapping("/login/google")
    public String googleLogin(@RequestParam String code, Model model, HttpServletRequest request) throws JsonProcessingException {
        log.info("code={}", code);

        Model userInfo = googleService.getUserInfo(code, model);
        Member googleUser = googleService.saveKakaoUser(userInfo);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, googleUser);

        return "redirect:/";

    }
}
