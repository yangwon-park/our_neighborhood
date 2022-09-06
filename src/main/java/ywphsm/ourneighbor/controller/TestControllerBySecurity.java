package ywphsm.ourneighbor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestControllerBySecurity {

    @GetMapping("/admin/test")
    @ResponseBody
    public String securityTest() {
        return "hello";
    }

    @GetMapping("/user/test")
    @ResponseBody
    public String securityTest2() {
        return "hello";
    }
}
