package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CashUserRepository extends JpaRepository<CashUser, Long> {
    Optional<CashUser> findCashUserById(Long id);
}