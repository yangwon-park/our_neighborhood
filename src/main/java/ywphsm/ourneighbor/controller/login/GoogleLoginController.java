package ywphsm.ourneighbor.controller.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.login.GoogleService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Slf4j
@Controller
public class GoogleLoginController {

    private final GoogleService googleService;

    @Value("${google.scope}")
    private String GOOGLE_SCOPE;

    @GetMapping("/login/googleUrl")
    public String googleUrl() {
        return "redirect:https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?scope=" + GOOGLE_SCOPE + "&response_type=code&client_id=1008060436788-dmq8hj575pafbfnajpmdkouao0lm2ucg.apps.googleusercontent.com&redirect_uri=http://localhost:8080/login/google";
    }

    @GetMapping("/login/google")
    public String googleLogin(@RequestParam String code, Model model, HttpServletRequest request) throws JsonProcessingException, ParseException {
        log.info("code={}", code);

        Member userInfo = googleService.getUserInfo(code, model);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, userInfo);

        return "redirect:/";

    }
}
