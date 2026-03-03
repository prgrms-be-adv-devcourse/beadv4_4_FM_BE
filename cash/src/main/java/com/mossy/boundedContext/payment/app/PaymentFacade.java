package com.mossy.boundedContext.payment.app;

import com.mossy.boundedContext.payment.app.usecase.PaymentTossFullRefundUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentTossPartialRefundUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentCashFullRefundUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentCashPartialRefundUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentConfirmCashUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentConfirmTossUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentFindAllUseCase;
import com.mossy.boundedContext.payment.app.usecase.PaymentRetrieveUseCase;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmCashRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmTossRequestDto;
import com.mossy.boundedContext.payment.in.dto.response.PaymentResponse;
import com.mossy.boundedContext.payment.in.dto.response.TossPaymentResponse;
import com.mossy.shared.cash.enums.PayMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentConfirmTossUseCase paymentConfirmTossUseCase;
    private final PaymentCashFullRefundUseCase paymentCancelCashUseCase;
    private final PaymentCashPartialRefundUseCase cashPartialRefundUseCase;
    private final PaymentConfirmCashUseCase paymentConfirmCashUseCase;
    private final PaymentTossFullRefundUseCase fullRefundUseCase;
    private final PaymentTossPartialRefundUseCase partialRefundUseCase;
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

    // 통합 결제 취소 분기 로직
    public void cancelPayment(PaymentCancelRequestDto request) {
        Payment payment = paymentSupport.findPayment(request.orderId());

        if (payment.getPayMethod() == PayMethod.CASH) {
            if (request.isPartialCancel()) {
                cashPartialRefundUseCase.execute(request.orderId(), request.cancelReason(), request.ids(), request.cancelAmount());
            } else {
                paymentCancelCashUseCase.execute(request.orderId(), request.cancelReason());
            }
        } else {
            // TOSS (CARD 등) 결제인 경우
            if (request.isPartialCancel()) {
                partialRefundUseCase.execute(request.orderId(), request.cancelReason(), request.ids(), request.cancelAmount());
            } else {
                fullRefundUseCase.execute(request.orderId(), request.cancelReason());
            }
        }
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
