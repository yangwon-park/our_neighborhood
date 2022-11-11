package ywphsm.ourneighbor.repository.recommendpost;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.api.dto.RecommendKind;
import ywphsm.ourneighbor.domain.RecommendPost;

import java.util.List;

public interface RecommendPostRepository extends JpaRepository<RecommendPost, Long>, RecommendPostRepositoryCustom {

    List<RecommendPost> findByRecommendKind(RecommendKind cond);
}
