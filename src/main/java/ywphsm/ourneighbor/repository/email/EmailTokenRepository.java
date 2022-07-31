package ywphsm.ourneighbor.repository.email;


import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.member.EmailToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, String> {

    Optional<EmailToken> findByIdAndExpirationDateAfterAndExpired(String emailTokenId, LocalDateTime now, boolean expired);
}
