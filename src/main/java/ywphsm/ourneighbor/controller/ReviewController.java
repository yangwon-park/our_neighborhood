package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.service.ReviewService;
import ywphsm.ourneighbor.service.StoreService;
import ywphsm.ourneighbor.service.login.SessionConst;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class ReviewController {

    @GetMapping("/store/{storeId}/createReview")
    public String createReview(@PathVariable Long storeId,
                               @ModelAttribute(name = "reviewDTO") ReviewDTO.Add reviewDTO,
                               @SessionAttribute(value = SessionConst.LOGIN_MEMBER) Member member) {

        reviewDTO.setStoreId(storeId);
        reviewDTO.setMemberId(member.getId());

        return "review/createReview";
    }

}
