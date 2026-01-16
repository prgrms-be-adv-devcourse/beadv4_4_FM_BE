package backend.mossy.boundedContext.cash.domain.history;

public enum EventType {
    충전__무통장입금,
    충전__PG결제_토스페이먼츠,
    사용__주문결제,
    임시보관__주문결제,
    정산지급__상품판매_수수료,
    정산수령__상품판매_수수료,
    정산지급__상품판매_대금,
    정산수령__상품판매_대금,
}
