package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.user.CashUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashUserRepository extends JpaRepository<CashUser, Long> {
}