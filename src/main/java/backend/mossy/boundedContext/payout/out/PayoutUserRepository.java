package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutUserRepository extends JpaRepository<PayoutUser, Long> {
}
