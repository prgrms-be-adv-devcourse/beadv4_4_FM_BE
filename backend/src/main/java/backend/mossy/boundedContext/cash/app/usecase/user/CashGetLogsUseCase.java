package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.domain.user.UserCashLog;
import backend.mossy.boundedContext.cash.out.user.UserCashLogRepository;
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
