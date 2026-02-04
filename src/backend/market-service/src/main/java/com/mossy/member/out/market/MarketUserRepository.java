package com.mossy.member.out.market;

import com.mossy.member.domain.market.MarketUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
}
