package backend.mossy.boundedContext.cash.out.user;

import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    boolean existsWalletByUserId(Long userId);

    Optional<UserWallet> findWalletByUserId(Long userId);
}
