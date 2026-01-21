package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    Optional<Payout> findByPayeeAndPayoutDateIsNull(PayoutSeller payee);

    List<Payout> findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(BigDecimal amount, Pageable pageable);
}
