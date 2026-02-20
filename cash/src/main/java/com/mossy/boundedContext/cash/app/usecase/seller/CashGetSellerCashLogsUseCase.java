package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.response.SellerCashLogResponseDto;
import com.mossy.boundedContext.cash.out.seller.SellerCashLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashGetSellerCashLogsUseCase {

    private final SellerCashLogRepository sellerCashLogRepository;
    private final CashMapper mapper;

    @Transactional(readOnly = true)
    public List<SellerCashLogResponseDto> findSellerCashLog(Long sellerId) {
        return sellerCashLogRepository.findAllBySellerIdOrderByCreatedAtDesc(sellerId)
            .stream()
            .map(mapper::toResponseDto)
            .toList();
    }
}
