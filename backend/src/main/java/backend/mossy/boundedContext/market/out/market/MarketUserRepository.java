package backend.mossy.boundedContext.market.out.market;

import backend.mossy.boundedContext.market.domain.market.MarketUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
}
