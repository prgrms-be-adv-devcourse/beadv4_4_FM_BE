package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.seller.SellerEventType;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CashHoldingUseCase {

    private final CashSupport cashSupport;

    public void holdPaymentAmount(PaymentCompletedEvent request) {
        UserWallet buyerWallet = cashSupport.findWalletByUserId(request.buyerId());
        SellerWallet holdingWallet = cashSupport.findHoldingWallet(); // 정책상 2번은 홀딩 지갑

        BigDecimal orderAmount = request.amount();

        if (buyerWallet.getBalance().compareTo(orderAmount) < 0) {
            throw new IllegalStateException("잔액이 부족하여 홀딩 처리를 할 수 없습니다.");
        }

        // 구매자 지갑에서 주문 금액 차감
        buyerWallet.debit(
            orderAmount,
            UserEventType.사용__주문결제,
            "ORDER",
            request.orderId()
        );

        // 홀딩 지갑으로 주문 금액 적립
        holdingWallet.credit(
            orderAmount,
            SellerEventType.임시보관__주문결제,
            "ORDER",
            request.orderId()
        );
    }
}