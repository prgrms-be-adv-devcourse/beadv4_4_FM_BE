package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.MarketSeller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketSellerRepository extends JpaRepository<MarketSeller, Long> {
}
