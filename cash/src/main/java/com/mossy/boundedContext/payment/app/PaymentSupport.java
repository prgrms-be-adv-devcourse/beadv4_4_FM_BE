package com.mossy.boundedContext.payment.app;

import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.event.PaymentCanceledEvent;
import com.mossy.boundedContext.payment.in.dto.request.TossConfirmRequest;
import com.mossy.boundedContext.payment.in.dto.response.TossCancelResponse;
import com.mossy.boundedContext.payment.in.dto.response.TossConfirmResponse;
import com.mossy.boundedContext.payment.out.MarketFeignClient;
import com.mossy.boundedContext.payment.out.PaymentRepository;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.exception.ErrorCode;
import com.mossy.exception.DomainException;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.enums.PaymentStatus;
import com.mossy.boundedContext.payment.in.dto.event.PaymentCancelFailedEvent;
import com.mossy.shared.market.enums.OrderState;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentSupport {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsService tossPaymentsService;
    private final ApplicationEventPublisher eventPublisher;
    private final MarketFeignClient marketFeignClient;

    // ── 결제 조회 ──

    public Payment findPayment(String orderNo) {
        return paymentRepository.findByOrderNoAndStatus(orderNo, PaymentStatus.PAID)
            .orElseThrow(() -> new DomainException(ErrorCode.PAID_PAYMENT_NOT_FOUND));
    }

    // ── 주문 조회 (FeignClient) ──

    public MarketOrderResponse findPendingOrder(String orderNo) {
        MarketOrderResponse order = marketFeignClient.getOrderByOrderNo(orderNo);
        if (order.status() != OrderState.PENDING) {
            throw new DomainException(ErrorCode.PENDING_ORDER_NOT_FOUND);
        }
        return order;
    }

    public MarketOrderResponse findOrderForCancel(Long orderId) {
        MarketOrderResponse order = marketFeignClient.getOrder(orderId);
        if (order.status() != OrderState.PAID) {
            throw new DomainException(ErrorCode.PAID_ORDER_NOT_FOUND);
        }
        return order;
    }

    // ── 금액 검증 ──

    public void validateAmount(BigDecimal orderAmount, BigDecimal requestAmount) {
        if (orderAmount.compareTo(requestAmount) != 0) {
            throw new DomainException(ErrorCode.ORDER_AMOUNT_MISMATCH);
        }
    }

    // ── 토스 API 호출 ──

    public TossConfirmResponse requestTossConfirm(String paymentKey, String orderId, BigDecimal amount) {
        TossConfirmRequest request = TossConfirmRequest.of(paymentKey, orderId, amount);
        return tossPaymentsService.confirm(request);
    }

    public void requestTossCancel(String paymentKey, String cancelReason) {
        try {
            TossCancelResponse response = tossPaymentsService.cancel(paymentKey, cancelReason);
            eventPublisher.publishEvent(new PaymentCanceledEvent(response));
        } catch (Exception e) {
            eventPublisher.publishEvent(new PaymentCancelFailedEvent(paymentKey, cancelReason));
        }
    }

    // ── 결제 저장 ──

    public Payment saveTossPayment(Long orderId, String paymentKey, String orderNo, BigDecimal amount) {
        Payment payment = Payment.createTossPaid(orderId, paymentKey, orderNo, amount, PayMethod.CARD);
        return paymentRepository.save(payment);
    }

    public Payment saveCashPayment(Long orderId, String orderNo, BigDecimal amount) {
        Payment payment = Payment.createCashPaid(orderId, orderNo, amount, PayMethod.CASH);
        return paymentRepository.save(payment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailure(Long orderId, String orderNo, String paymentKey, BigDecimal amount,
                            PayMethod method, String failReason) {
        Payment failed = Payment.createFailed(orderId, paymentKey, orderNo, amount, method, failReason);
        paymentRepository.save(failed);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processCancel(String orderNo, String paymentKey, BigDecimal amount,
        PayMethod method, String cancelReason) {
        Payment canceled = Payment.createCanceled(null, paymentKey, orderNo, amount, method, cancelReason);
        paymentRepository.save(canceled);
    }

    // ── 유틸 ──

    public static String resolveOriginalOrderNo(String pgOrderId) {
        if (pgOrderId == null) return null;
        int index = pgOrderId.indexOf("__");
        if (index > 0) {
            return pgOrderId.substring(0, index);
        }
        return pgOrderId;
    }
}
