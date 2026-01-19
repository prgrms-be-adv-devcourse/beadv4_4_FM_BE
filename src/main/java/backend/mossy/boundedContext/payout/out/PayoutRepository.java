package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.Payout;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long> {

    Optional<Payout> findByPayeeAndPayoutDateIsNull(PayoutSeller payee);

}
