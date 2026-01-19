package backend.mossy.boundedContext.payout.out;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutSellerRepository extends JpaRepository<PayoutSeller, Long> {
    Optional<PayoutSeller> findByStoreName(String username);
}
