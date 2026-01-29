package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.OrderState;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.boundedContext.market.domain.payment.PaymentStatus;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.event.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new DomainException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getState() != OrderState.PAID) {
            throw new DomainException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        Payment paidPayment = order.getPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .findFirst()
            .orElseThrow(() -> new DomainException(ErrorCode.PAID_PAYMENT_NOT_FOUND));

        eventPublisher.publish(new OrderCancelEvent(
            order.getOrderNo(),
            order.getBuyer().getId(),
            paidPayment.getAmount(),
            paidPayment.getPayMethod(),
            cancelReason
        ));
    }
}
