package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.app.PayoutSupport;
import com.mossy.boundedContext.payout.domain.calculator.DistanceCalculator;
import com.mossy.boundedContext.payout.domain.calculator.WeightCalculator;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.market.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PayoutHandleOrderPaidUseCase {

    private final PayoutSupport payoutSupport;
    private final DistanceCalculator distanceCalculator;
    private final WeightCalculator weightCalculator;
    private final PayoutMapper payoutMapper;
    private final PayoutAddPayoutCandidateItemsUseCase payoutAddPayoutCandidateItemsUseCase;

    public void handle(OrderPaidEvent event) {
        PayoutUser buyer = payoutSupport.findUserById(event.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.BUYER_NOT_FOUND));

        event.orderItems().forEach(orderItem -> {
            PayoutSeller seller = payoutSupport.findSellerById(orderItem.sellerId())
                    .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

            BigDecimal deliveryDistance = distanceCalculator.calculateDistance(
                    buyer.getLatitude(), buyer.getLongitude(),
                    seller.getLatitude(), seller.getLongitude()
            );

            String weightGrade = weightCalculator.determineWeightGrade(orderItem.weight());

            PayoutCandidateCreateDto dto =
                    payoutMapper.toCreatePayoutCandidateDto(event, orderItem, deliveryDistance, weightGrade);

            payoutAddPayoutCandidateItemsUseCase.addPayoutCandidateItem(dto);
        });
    }
}

