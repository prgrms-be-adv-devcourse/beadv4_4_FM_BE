package backend.mossy.boundedContext.cash.app.usecase.user;

import backend.mossy.boundedContext.cash.app.CashSupport;
import backend.mossy.boundedContext.cash.domain.seller.SellerEventType;
import backend.mossy.boundedContext.cash.domain.seller.SellerWallet;
import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import backend.mossy.shared.market.event.PaymentRefundEvent;
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
            throw new DomainException(ErrorCode.INSUFFICIENT_BALANCE);
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

    public void processRefund(PaymentRefundEvent event) {
        SellerWallet holdingWallet = cashSupport.findHoldingWallet();
        BigDecimal refundAmount = event.amount();

        // 홀딩 지갑에서 환불 금액 차감
        holdingWallet.debit(
            refundAmount,
            SellerEventType.보관해제__주문취소,
            "ORDER_CANCEL",
            event.orderId()
        );

        // 예치금 결제인 경우에만 구매자 지갑으로 환불 (Toss 결제는 PG사가 환불 처리)
        if (event.payMethod() == PayMethod.CASH) {
            UserWallet buyerWallet = cashSupport.findWalletByUserId(event.buyerId());
            buyerWallet.credit(
                refundAmount,
                UserEventType.환불__결제취소,
                "ORDER_CANCEL",
                event.orderId()
            );
        }
    }
}