package com.mossy.boundedContext.cash.out.seller;

import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SellerWalletRepository extends JpaRepository<SellerWallet, Long> {

    boolean existsBySellerId(Long sellerId);

    Optional<SellerWallet> findWalletBySellerId(Long sellerId);

    Optional<SellerWallet> findBySellerId(Long sellerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from SellerWallet w where w.seller.id = :sellerId")
    Optional<SellerWallet> findWalletBySellerIdForUpdate(@Param("sellerId") Long sellerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from SellerWallet w where w.seller.id = :sellerId")
    Optional<SellerWallet> findBySellerIdForUpdate(@Param("sellerId") Long sellerId);
}
