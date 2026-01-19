package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.seller.SellerCashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerCashLogRepository extends JpaRepository<SellerCashLog, Long> {

}
