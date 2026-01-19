package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.user.UserCashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCashLogRepository extends JpaRepository<UserCashLog, Long> {

}
