package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutUserRepository extends JpaRepository<PayoutUser, Long> {
}
