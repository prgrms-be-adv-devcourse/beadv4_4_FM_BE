package backend.mossy.boundedContext.cash.out.seller;

import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerWalletRepository extends JpaRepository<SellerWallet, Long> {

    boolean existsBySellerId(Long sellerId);

    Optional<SellerWallet> findWalletBySellerId(Long sellerId);

    Optional<SellerWallet> findByHolderId(Long holdingMemberId);
}
