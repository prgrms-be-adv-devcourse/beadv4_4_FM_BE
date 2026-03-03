package com.mossy.boundedContext.marketUser.out;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketSellerRepository extends JpaRepository<MarketSeller, Long> {
    Optional<MarketSeller> findByUserId(Long userId);
}
