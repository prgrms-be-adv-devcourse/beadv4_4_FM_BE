package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.shared.market.dto.toss.PaymentCancelCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmTossRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentConfirmTossUseCase paymentConfirmUseCase;
    private final PaymentCancelCashUseCase paymentCancelUserUseCase;
    private final PaymentConfirmCashUseCase paymentConfirmCashUserCase;
    private final PaymentCancelTossUseCase paymentCancelTossUseCase;

    // PG 결제 승인
    public void confirmTossPayment(PaymentConfirmTossRequestDto request) {
        paymentConfirmUseCase.confirmToss(request);
    }

    // 예치금 결제 승인
    public void confirmCashPayment(PaymentConfirmCashRequestDto request) {
        paymentConfirmCashUserCase.confirmCash(request);
    }

    // PG 결제 취소
    public void cancelTossPayment(PaymentCancelTossRequestDto request) {
        paymentCancelTossUseCase.cancelTossPayment(request);
    }

    // 예치금 결제 취소
    public void cancelCashPayment(PaymentCancelCashRequestDto request) {
        paymentCancelUserUseCase.cancelCashPayment(request);
    }
}
