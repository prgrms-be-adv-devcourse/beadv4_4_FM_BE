package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutMemberRepository extends JpaRepository<PayoutUser, Long> {
}
