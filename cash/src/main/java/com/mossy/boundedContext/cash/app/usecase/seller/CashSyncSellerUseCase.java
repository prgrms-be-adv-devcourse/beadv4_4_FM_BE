package com.mossy.boundedContext.cash.app.usecase.seller;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;
import com.mossy.boundedContext.cash.out.seller.CashSellerRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.boundedContext.cash.in.dto.event.CashSellerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashSyncSellerUseCase {

    private final CashSellerRepository cashSellerRepository;
    private final EventPublisher eventPublisher;
    private final CashMapper mapper;

    @Transactional
    public void syncSeller(CashSellerDto seller) {
        cashSellerRepository.findById(seller.sellerId())
            .ifPresentOrElse(
                existingSeller -> existingSeller.update(seller),

                () -> {
                    CashSeller newSeller = cashSellerRepository.save(mapper.toEntity(seller));
                    eventPublisher.publish(
                        new CashSellerCreatedEvent(mapper.toDto(newSeller))
                    );
                }
            );
    }
}