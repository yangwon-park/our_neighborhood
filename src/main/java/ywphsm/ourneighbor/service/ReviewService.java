package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.repository.review.ReviewRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public Long saveReview(Review review) {
        reviewRepository.save(review);
        return review.getId();
    }

   public Review findOne(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(NoSuchElementException::new);
   }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

}
