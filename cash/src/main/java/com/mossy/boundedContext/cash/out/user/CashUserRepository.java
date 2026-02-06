package com.mossy.boundedContext.cash.out.user;

import com.mossy.boundedContext.cash.domain.user.CashUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashUserRepository extends JpaRepository<CashUser, Long> {

    Optional<CashUser> findCashUserById(Long id);

}