package backend.mossy.boundedContext.market.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    READY("결제준비"),
    PAID("결제완료"),
    FAILED("결제실패"),
    CANCELED("전체취소"),
    PARTIAL_CANCELED("부분취소"),
    REFUND_IN_PROGRESS("환불진행중");

    private final String description;
}