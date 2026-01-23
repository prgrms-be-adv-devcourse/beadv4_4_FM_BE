package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCancelTossUseCase {

    private final PaymentSupport paymentSupport;

    @Transactional
    public void cancelTossPayment(PaymentCancelTossRequestDto request) {
        String orderNo = request.orderId();
        String paymentKey = request.paymentKey();
        BigDecimal cancelAmount = request.cancelAmount();
        String cancelReason = request.cancelReason();

        paymentSupport.findOrderForCancel(orderNo);
        paymentSupport.requestTossCancel(paymentKey, cancelReason);
        paymentSupport.processCancel(orderNo, paymentKey,cancelAmount, PayMethod.CARD, cancelReason);
    }
}
