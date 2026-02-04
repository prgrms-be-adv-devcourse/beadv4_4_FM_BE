package com.mossy.member.out.user;

import com.mossy.member.domain.user.UserCashLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCashLogRepository extends JpaRepository<UserCashLog, Long> {
    List<UserCashLog> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
