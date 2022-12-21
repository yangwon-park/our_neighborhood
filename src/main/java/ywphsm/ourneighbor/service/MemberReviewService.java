package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.repository.member.MemberRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberReviewService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ReviewService reviewService;

    //회원탈퇴
    @Transactional
    public void withdrawal(Long id) {
        Member findMember = memberService.findById(id);
        List<Review> reviewList = findMember.getReviewList();

        for (Review review : reviewList) {
            reviewService.delete(review.getStore().getId(), review.getId());
        }

        memberRepository.delete(findMember);
    }

    @Transactional
    public boolean adminWithdrawal(String userId) {
        Optional<Member> findMember = memberRepository.findByUserId(userId);
        if (findMember.isPresent()) {
            List<Review> reviewList = findMember.get().getReviewList();
            for (Review review : reviewList) {
                reviewService.delete(review.getStore().getId(), review.getId());
            }
            findMember.ifPresent(memberRepository::delete);
            return true;
        }
        return false;
    }
}
