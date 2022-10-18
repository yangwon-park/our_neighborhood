package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.repository.review.ReviewRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    private final AwsS3FileStore awsS3FileStore;

    private final FileStore fileStore;

    @Transactional
    public Long save(ReviewDTO.Add reviewAddDTO) throws IOException {
        Store linkedStore = storeRepository.findById(reviewAddDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));
        Member linkedMember = memberRepository.findById(reviewAddDTO.getMemberId()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 없어요"));

//        UploadFile newUploadFile = fileStore.storeFile(reviewAddDTO.getFile());
        UploadFile newUploadFile = awsS3FileStore.storeFile(reviewAddDTO.getFile());
        log.info("fileName={}", newUploadFile.getStoredFileName());
        log.info("fileName={}", newUploadFile.getUploadedFileName());

        Review review = reviewAddDTO.toEntity(linkedStore, linkedMember);
        newUploadFile.addReview(review);
        linkedStore.addReview(review);
        linkedMember.addReview(review);

        return reviewRepository.save(review).getId();
    }

    @Transactional
    public Long delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 리뷰입니다. id = " + reviewId));

        log.info("review={}", review);

        reviewRepository.delete(review);

        return reviewId;
    }

   public Review findOne(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(NoSuchElementException::new);
   }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public Slice<ReviewMemberDTO> pagingReview(Long storeId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        Slice<ReviewMemberDTO> reviewMemberDTOS = reviewRepository.reviewPage(pageRequest, storeId);
        log.info("reviewMemberDTO={}", reviewMemberDTOS);


        return reviewMemberDTOS;
    }

    public List<ReviewMemberDTO> myReviewList(Long memberId) {
        List<ReviewMemberDTO> reviewMemberDTOS = reviewRepository.myReview(memberId);
        log.info("reviewMemberDTO={}", reviewMemberDTOS);

        return reviewMemberDTOS;
    }

    public long myReviewCount(Long memberId) {
        return reviewRepository.myReviewCount(memberId);
    }

    public double ratingAverage(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));

        if (store.getReviewList().size() == 0) {
            return 0;
        }

        double ratingTotal = store.getRatingTotal();
        double count = store.getReviewList().size();
        double average = ratingTotal / count;

        return Math.round(average * 10) / 10.0;

    }

    @Transactional
    public void ratingDiscount(Long storeId, Long reviewId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));
        Review review = findOne(reviewId);
        store.reviewDelete(review.getRating());
    }
}
