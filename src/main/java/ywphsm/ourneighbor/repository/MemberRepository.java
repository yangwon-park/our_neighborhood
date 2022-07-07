package ywphsm.ourneighbor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
