package ywphsm.ourneighbor.repository.recommendpost;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.RecommendPost;

public interface RecommendPostRepository extends JpaRepository<RecommendPost, Long>, RecommendPostRepositoryCustom {
}
