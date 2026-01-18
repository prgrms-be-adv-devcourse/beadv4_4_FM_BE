package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutUserRepository extends JpaRepository<PayoutUser, Long> {
    Optional<PayoutUser> findByUsername(String username);
}
