package ywphsm.ourneighbor.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryCustom {

    boolean existsByName(String name);

    Hashtag findByName(String name);
}
