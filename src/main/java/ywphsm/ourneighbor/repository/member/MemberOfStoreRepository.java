package ywphsm.ourneighbor.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.MemberOfStore;

import java.util.Optional;

public interface MemberOfStoreRepository extends JpaRepository<MemberOfStore, Long> {

    Optional<MemberOfStore> findByStoreIdAndMemberId(Long storeId, Long memberId);
}
