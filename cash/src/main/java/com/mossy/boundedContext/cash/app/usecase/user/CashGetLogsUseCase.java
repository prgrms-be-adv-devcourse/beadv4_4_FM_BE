package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.out.user.UserCashLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetLogsUseCase {

    private final UserCashLogRepository userCashLogRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public List<UserCashLogResponseDto> findCashLog(Long userId) {
        return userCashLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(mapper::toResponseDto)
            .toList();
    }
}
