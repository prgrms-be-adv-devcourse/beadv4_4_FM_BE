package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.MarketUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
}
