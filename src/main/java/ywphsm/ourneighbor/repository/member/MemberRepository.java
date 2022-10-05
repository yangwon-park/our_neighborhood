package ywphsm.ourneighbor.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

}
