package com.mossy.boundedContext.app.usecase.user;

import com.mossy.boundedContext.domain.user.UserCashLog;
import com.mossy.boundedContext.out.user.UserCashLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetLogsUseCase {

    private final UserCashLogRepository userCashLogRepository;

    public List<UserCashLog> findCashLog(Long userId) {
        return userCashLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
