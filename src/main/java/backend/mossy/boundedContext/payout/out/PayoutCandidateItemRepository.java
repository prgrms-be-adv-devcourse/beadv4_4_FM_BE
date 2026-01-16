package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutCandidateItemRepository extends JpaRepository<PayoutCandidateItem, Long> {

}
