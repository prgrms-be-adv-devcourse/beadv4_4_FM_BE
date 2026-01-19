package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsWalletByUser_Id(Long userId);

    Optional<Wallet> findWalletByUser_Id(Long userId);
}
