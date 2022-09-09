package ywphsm.ourneighbor.controller.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ywphsm.ourneighbor.controller.form.LoginForm;


@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm) {
        return "login/loginForm";
    }
}