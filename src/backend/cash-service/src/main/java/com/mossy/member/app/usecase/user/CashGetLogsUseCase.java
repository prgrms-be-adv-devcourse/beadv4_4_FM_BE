package com.mossy.member.app.usecase.user;

import com.mossy.member.domain.user.UserCashLog;
import com.mossy.member.out.user.UserCashLogRepository;
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
