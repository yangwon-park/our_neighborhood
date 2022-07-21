package ywphsm.ourneighbor.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByNickname(String nickname);
}
