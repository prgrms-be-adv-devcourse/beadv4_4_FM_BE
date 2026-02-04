package com.mossy.member.out.seller;

import com.mossy.member.domain.seller.SellerWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerWalletRepository extends JpaRepository<SellerWallet, Long> {

    boolean existsBySellerId(Long sellerId);

    Optional<SellerWallet> findWalletBySellerId(Long sellerId);

    Optional<SellerWallet> findBySellerId(Long sellerId);
}
