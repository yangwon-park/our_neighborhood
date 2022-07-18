package ywphsm.ourneighbor.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
