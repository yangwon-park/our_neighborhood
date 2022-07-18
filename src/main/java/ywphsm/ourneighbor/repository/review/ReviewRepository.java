package ywphsm.ourneighbor.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
}
