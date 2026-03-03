package com.mossy.boundedContext.cash.out.user;

import com.mossy.boundedContext.cash.domain.user.UserCashLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCashLogRepository extends JpaRepository<UserCashLog, Long> {
    Page<UserCashLog> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
