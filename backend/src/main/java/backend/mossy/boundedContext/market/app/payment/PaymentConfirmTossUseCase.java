package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.toss.PaymentConfirmTossRequestDto;
import backend.mossy.shared.market.dto.toss.TossConfirmResponse;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConfirmTossUseCase {

    private final EventPublisher eventPublisher;
    private final PaymentSupport paymentSupport;

    @Transactional
    public void confirmToss(PaymentConfirmTossRequestDto request) {
        String orderNo = paymentSupport.resolveOriginalOrderNo(request.orderId());
        Order order = paymentSupport.findPendingOrder(orderNo);

        // 1. 주문 금액 검증
        try {
            order.validateAmount(request.amount());
        } catch (Exception e) {
            paymentSupport.saveFailure(
                orderNo, request.paymentKey(), request.amount(),
                PayMethod.CARD, "주문 금액 검증 실패: " + e.getMessage()
            );
            throw e;
        }
        // 2. PG(토스) 승인 요청
        TossConfirmResponse tossResponse;
        try {
            tossResponse = paymentSupport.requestTossConfirm(
                request.paymentKey(), request.orderId(), request.amount()
            );
        } catch (Exception e) {
            paymentSupport.saveFailure(
                orderNo, request.paymentKey(), request.amount(),
                PayMethod.CARD, "PG 승인 실패: " + e.getMessage()
            );
            throw e;
        }
        // 3. 결제 완료 처리 및 이벤트 발행
        try {
            LocalDateTime paymentDate = order.createTossPayment(tossResponse.paymentKey(), tossResponse.orderId(), tossResponse.totalAmount(), PayMethod.CARD);
            eventPublisher.publish(new PaymentCompletedEvent(
                order.getId(),
                order.getBuyer().getId(),
                paymentDate,
                order.getTotalPrice(),
                String.valueOf(request.payMethod())
            ));
        } catch (Exception e) {
            // 시스템 오류 시 PG 결제 취소
            paymentSupport.requestTossCancel(tossResponse.paymentKey(), "시스템 오류로 인한 결제 취소");

            // DB 취소 이력 저장
            paymentSupport.processCancel(
                orderNo, tossResponse.paymentKey(), request.amount(),
                PayMethod.CARD, "시스템 오류로 인한 결제 취소: " + e.getMessage()
            );
            throw e;
        }
    }
}
