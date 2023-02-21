package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.member.login.SessionConst;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class ReviewController {

    @GetMapping("/store/{storeId}/create-review")
    public String createReview(@PathVariable Long storeId,
                               @ModelAttribute(name = "reviewDTO") ReviewDTO.Add reviewDTO,
                               @SessionAttribute(value = SessionConst.LOGIN_MEMBER) Member member) {

        reviewDTO.setStoreId(storeId);
        reviewDTO.setMemberId(member.getId());

        return "review/create_review";
    }

}
