package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PayoutCandidateItemRepository extends JpaRepository<PayoutCandidateItem, Long> {
    List<PayoutCandidateItem> findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(LocalDateTime paymentDate, Pageable pageable);
}
