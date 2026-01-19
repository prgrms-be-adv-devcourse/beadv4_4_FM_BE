package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutSellerRepository extends JpaRepository<PayoutSeller, Long> {
}
