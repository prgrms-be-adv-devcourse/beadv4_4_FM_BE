package com.mossy.member.out.seller;

import com.mossy.member.domain.seller.CashSeller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashSellerRepository extends JpaRepository<CashSeller, Long> {

    Optional<CashSeller> findCashSellerById(Long sellerId);

}
