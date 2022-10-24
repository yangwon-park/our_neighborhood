package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ywphsm.ourneighbor.controller.form.LoginForm;


@RequiredArgsConstructor
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm) {
        return "login/loginForm";
    }
}