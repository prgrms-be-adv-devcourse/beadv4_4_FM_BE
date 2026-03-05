package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.app.mapper.PaymentMapper;
import com.mossy.boundedContext.payment.in.dto.response.PaymentResponse;
import com.mossy.boundedContext.payment.out.PaymentRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentFindAllUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPayment(String orderNo, Pageable pageable) {
        String realOrderNo = PaymentSupport.resolveOriginalOrderNo(orderNo);

        Page<PaymentResponse> payments = paymentRepository.findByOrderNoContaining(realOrderNo, pageable)
            .map(paymentMapper::toPaymentResponse);

        if (payments.isEmpty()) {
            throw new DomainException(ErrorCode.PAID_PAYMENT_NOT_FOUND);
        }

        return payments;
    }
}
