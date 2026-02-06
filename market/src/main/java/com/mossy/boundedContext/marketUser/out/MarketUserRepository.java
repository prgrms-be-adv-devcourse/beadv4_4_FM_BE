package com.mossy.boundedContext.marketUser.out;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
}
