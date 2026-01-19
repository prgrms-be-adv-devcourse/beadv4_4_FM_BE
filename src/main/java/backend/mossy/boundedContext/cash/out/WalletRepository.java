package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsWalletByUserId(Long userId);

    Optional<Wallet> findWalletByUserId(Long userId);
}
