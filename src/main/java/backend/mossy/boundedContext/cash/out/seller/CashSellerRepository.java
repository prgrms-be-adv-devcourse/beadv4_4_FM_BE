package backend.mossy.boundedContext.cash.out.seller;

import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashSellerRepository extends JpaRepository<CashSeller, Long> {

    Optional<CashSeller> findCashSellerById(Long sellerId);

}
