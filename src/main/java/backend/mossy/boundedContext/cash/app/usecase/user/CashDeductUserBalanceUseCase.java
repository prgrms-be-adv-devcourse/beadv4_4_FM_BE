package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.cash.dto.request.UserDeductBalanceRequest;
import backend.mossy.shared.cash.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashDeductUserBalanceUseCase {

    private final CashSupport cashSupport;
    private final EventPublisher eventPublisher;

    public void deduct(UserDeductBalanceRequest request) {
        UserWallet wallet = cashSupport.findWalletByUserId(request.userId());

        wallet.debit(request.amount(), request.eventType(), "Order", request.orderId());

        // 성공 이벤트 발행 (이후 Payment 상태를 PAID로 변경)
        eventPublisher.publish(new PaymentCompletedEvent(request.orderId()));
    }
}
