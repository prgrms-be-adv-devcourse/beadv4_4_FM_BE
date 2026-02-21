package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.response.SellerCashLogResponseDto;
import com.mossy.boundedContext.cash.out.seller.SellerCashLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetSellerCashLogsUseCase {

    private final SellerCashLogRepository sellerCashLogRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public Page<SellerCashLogResponseDto> findSellerCashLog(Long sellerId, Pageable pageable) {
        return sellerCashLogRepository.findAllBySellerIdOrderByCreatedAtDesc(sellerId, pageable)
            .map(mapper::toResponseDto);
    }
}
