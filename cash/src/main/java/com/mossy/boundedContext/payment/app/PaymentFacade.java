package com.mossy.boundedContext.payment.app;

import com.mossy.boundedContext.payment.app.usecase.PaymentCancelCashUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentCancelTossUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentConfirmCashUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentConfirmTossUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentFindAllUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentRetrieveUseCase;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelCashRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelTossRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmCashRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmTossRequestDto;
import com.mossy.boundedContext.payment.in.dto.response.PaymentResponse;
import com.mossy.boundedContext.payment.in.dto.response.TossPaymentResponse;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.market.event.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentConfirmTossUseCase paymentConfirmTossUseCase;
    private final PaymentCancelCashUseCase paymentCancelCashUseCase;
    private final PaymentConfirmCashUseCase paymentConfirmCashUseCase;
    private final PaymentCancelTossUseCase paymentCancelTossUseCase;
    private final PaymentFindAllUseCase paymentGetInfoUseCase;
    private final PaymentRetrieveUseCase paymentRetrieveUseCase;
    private final PaymentSupport paymentSupport;

    // PG 결제 승인
    public void confirmTossPayment(PaymentConfirmTossRequestDto request) {
        paymentConfirmTossUseCase.confirmToss(request);
    }

    // 예치금 결제 승인
    public void confirmCashPayment(PaymentConfirmCashRequestDto request) {
        paymentConfirmCashUseCase.confirmCash(request);
    }

    // PG 결제 취소
    public void cancelTossPayment(PaymentCancelTossRequestDto request) {
        paymentCancelTossUseCase.cancelTossPayment(request);
    }

    // 주문 취소로 인한 결제 취소
    public void orderCancelPayment(OrderCancelEvent event) {
        Payment payment = paymentSupport.findPayment(event.orderNo());
        PayMethod payMethod = payment.getPayMethod();

        if (payMethod == PayMethod.CARD) {
            paymentCancelTossUseCase.orderCancelTossPayment(event);
        } else if (payMethod == PayMethod.CASH) {
            paymentCancelCashUseCase.orderCancelCashPayment(event);
        }
    }

    // 예치금 결제 취소
    public void cancelCashPayment(PaymentCancelCashRequestDto request) {
        paymentCancelCashUseCase.cancelCashPayment(request);
    }

    // PG-승인된 결제 내역 조회
    public TossPaymentResponse findTossPayment(String orderNo) {
        return paymentRetrieveUseCase.getTossPaymentInfo(orderNo);
    }

    // 주문별 결제 내역 조회
    public Page<PaymentResponse> findAllPayments(String orderNo, Pageable pageable) {
        return paymentGetInfoUseCase.findAllPayment(orderNo, pageable);
    }
}
