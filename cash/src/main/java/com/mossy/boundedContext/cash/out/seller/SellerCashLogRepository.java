package com.mossy.boundedContext.cash.out.seller;

import com.mossy.boundedContext.cash.domain.seller.SellerCashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerCashLogRepository extends JpaRepository<SellerCashLog, Long> {

}
