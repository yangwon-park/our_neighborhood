package ywphsm.ourneighbor.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryCustom {

    boolean existsByName(String name);

    Optional<Hashtag> findByName(String name);
}
