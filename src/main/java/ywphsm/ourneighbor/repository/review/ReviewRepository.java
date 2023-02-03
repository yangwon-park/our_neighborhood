package ywphsm.ourneighbor.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.store.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    List<Review> findByContent(String content);
}
