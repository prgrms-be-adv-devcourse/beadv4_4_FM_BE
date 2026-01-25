package backend.mossy.boundedContext.market.out.market;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketSellerRepository extends JpaRepository<MarketSeller, Long> {
    Optional<MarketSeller> findByUserId(Long userId);
}
