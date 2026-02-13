package com.mossy.boundedContext.cash.app.usecase.common;

import com.mossy.boundedContext.cash.app.CashSupport;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.in.dto.request.CashHoldingRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.CashRefundRequestDto;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.enums.PayMethod;

import com.mossy.shared.cash.enums.SellerEventType;
import com.mossy.shared.cash.enums.UserEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashHoldingUseCase {

    private final CashSupport cashSupport;

    @Transactional
    public void holdPaymentAmount(CashHoldingRequestDto request) {
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

    @Transactional
    public void processRefund(CashRefundRequestDto request) {
        SellerWallet holdingWallet = cashSupport.findHoldingWallet();
        BigDecimal refundAmount = request.amount();

        // 홀딩 지갑에서 환불 금액 차감
        holdingWallet.debit(
            refundAmount,
            SellerEventType.보관해제__주문취소,
            "ORDER_CANCEL",
            request.orderId()
        );

        // 예치금 결제인 경우에만 구매자 지갑으로 환불 (Toss 결제는 PG사가 환불 처리)
        if (request.payMethod() == PayMethod.CASH) {
            UserWallet buyerWallet = cashSupport.findWalletByUserId(request.buyerId());
            buyerWallet.credit(
                refundAmount,
                UserEventType.환불__결제취소,
                "ORDER_CANCEL",
                request.orderId()
            );
        }
    }
}