package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.shared.market.dto.response.PaymentResponse;
import backend.mossy.shared.market.dto.toss.PaymentCancelCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmTossRequestDto;
import backend.mossy.shared.market.dto.toss.TossPaymentResponse;
import backend.mossy.shared.market.event.OrderCancelEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentConfirmTossUseCase paymentConfirmUseCase;
    private final PaymentCancelCashUseCase paymentCancelCashUseCase;
    private final PaymentConfirmCashUseCase paymentConfirmCashUserCase;
    private final PaymentCancelTossUseCase paymentCancelTossUseCase;
    private final PaymentFindAllUseCase paymentGetInfoUseCase;
    private final PaymentRetrieveUseCase paymentRetrieveUseCase;
    private final PaymentSupport paymentSupport;

    // PG 결제 승인
    public void confirmTossPayment(PaymentConfirmTossRequestDto request) {
        paymentConfirmUseCase.confirmToss(request);
    }

    // 예치금 결제 승인
    public void confirmCashPayment(PaymentConfirmCashRequestDto request) {
        paymentConfirmCashUserCase.confirmCash(request);
    }

    // PG 결제 취소
    public void cancelTossPayment(PaymentCancelTossRequestDto event) {
        paymentCancelTossUseCase.cancelTossPayment(event);
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
    @Transactional(readOnly = true)
    public TossPaymentResponse findTossPayment(String orderNo) {
        return paymentRetrieveUseCase.getTossPaymentInfo(orderNo);
    }

    // 주문별 결제 내역 조회
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAllPayments(String orderNo) {
        return paymentGetInfoUseCase.findAllPayment(orderNo);
    }
}
