package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.TossPaymentsService;
import com.mossy.boundedContext.payment.in.dto.response.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentRetrieveUseCase {

    private final TossPaymentsService tossPaymentsService;

    // 단순 조회 기능
    @Transactional(readOnly = true)
    public TossPaymentResponse getTossPaymentInfo(String orderId) {
        return tossPaymentsService.getPaymentByOrderId(orderId);
    }
}