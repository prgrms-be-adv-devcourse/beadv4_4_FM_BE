package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerWalletRepository extends JpaRepository<SellerWallet, Long> {

}
