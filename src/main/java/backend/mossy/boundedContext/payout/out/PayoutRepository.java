package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, Long> {

}
