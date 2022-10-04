package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.ReviewForm;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.review.ReviewRepository;
import ywphsm.ourneighbor.service.ReviewService;
import ywphsm.ourneighbor.service.StoreService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class ReviewController {

    private final ReviewService reviewService;

    private final StoreService storeService;

    private final ReviewRepository reviewRepository;

    @GetMapping("/store/{storeId}/createReview")
    public String createReview(@PathVariable Long storeId, @ModelAttribute ReviewForm reviewForm) {
        return "review/createReview";
    }

    @PostMapping("/store/{storeId}/createReview")
    public String createReview(@PathVariable Long storeId,
                               @Valid @ModelAttribute ReviewForm reviewForm,
                               BindingResult bindingResult,
                               @SessionAttribute(value = SessionConst.LOGIN_MEMBER) Member member) {

        if (bindingResult.hasErrors()) {
            return "review/createReview";
        }

        Store store = storeService.findOne(storeId);
        Review review = new Review(reviewForm.getContent(), reviewForm.getRating(), member, store);
        reviewService.saveReview(review);

        return "redirect:/store/{storeId}";
    }


}
