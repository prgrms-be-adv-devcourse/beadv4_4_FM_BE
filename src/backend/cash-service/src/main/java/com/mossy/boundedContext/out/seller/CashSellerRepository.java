package com.mossy.boundedContext.out.seller;

import com.mossy.boundedContext.domain.seller.CashSeller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashSellerRepository extends JpaRepository<CashSeller, Long> {

    Optional<CashSeller> findCashSellerById(Long sellerId);

}
