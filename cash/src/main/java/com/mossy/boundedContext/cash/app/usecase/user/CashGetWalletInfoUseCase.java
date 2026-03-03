package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetWalletInfoUseCase {

    private final UserWalletRepository userWalletRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public UserWalletResponseDto getUserWalletInfo(Long userId) {
        return userWalletRepository.findWalletByUserId(userId)
            .map(mapper::toResponseDto)
            .orElseThrow(() -> new DomainException(ErrorCode.USER_WALLET_NOT_FOUND));
    }
}