package ywphsm.ourneighbor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ywphsm.ourneighbor.service.NaverService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NaverLoginController {

    private final NaverService naverService;

    @GetMapping("/login/naverUrl")
    public String googleUrl() {
        return "redirect:https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=z_c0ETHIqnzInQwcpjLX&redirect_uri=http://localhost:8080/login/naver&state=1234";
    }

    @GetMapping("/login/naver")
    public String NaverLogin(@RequestParam String code, @RequestParam String state) throws JsonProcessingException {
        log.info("code:{}", code);

        naverService.getUserInfo(code, state);

        return "redirect:/";
    }

}
