package com.mossy.boundedContext.out.market;

import com.mossy.boundedContext.domain.market.MarketUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
}
