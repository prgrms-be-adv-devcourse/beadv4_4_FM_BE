package com.mossy.boundedContext.app.payment;

import com.mossy.shared.market.dto.toss.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentRetrieveUseCase {

    private final TossPaymentsService tossPaymentsService;

    // 단순 조회 기능
    public TossPaymentResponse getTossPaymentInfo(String orderId) {
        return tossPaymentsService.getPaymentByOrderId(orderId);
    }
}