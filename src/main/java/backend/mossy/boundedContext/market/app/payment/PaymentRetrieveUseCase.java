package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.shared.market.dto.toss.TossPaymentResponse;
import backend.mossy.shared.market.out.TossPaymentsService;
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