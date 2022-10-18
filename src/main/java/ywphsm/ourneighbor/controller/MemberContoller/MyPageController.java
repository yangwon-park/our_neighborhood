package ywphsm.ourneighbor.controller.MemberContoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import ywphsm.ourneighbor.domain.dto.MemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.login.SessionConst;

@RequiredArgsConstructor
@Controller
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/user/myPage")
    public String myPage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                         Model model) {

        Member byId = memberService.findById(member.getId());
        MemberDTO.Detail detail = new MemberDTO.Detail(byId);
        model.addAttribute("memberDetail", detail);
        return "member/myPage";
    }
}
