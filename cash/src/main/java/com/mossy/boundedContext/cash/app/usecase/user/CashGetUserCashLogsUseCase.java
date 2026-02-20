package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.out.user.UserCashLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetUserCashLogsUseCase {

    private final UserCashLogRepository userCashLogRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public Page<UserCashLogResponseDto> findUserCashLog(Long userId, Pageable pageable) {
        return userCashLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(mapper::toResponseDto);
    }
}
