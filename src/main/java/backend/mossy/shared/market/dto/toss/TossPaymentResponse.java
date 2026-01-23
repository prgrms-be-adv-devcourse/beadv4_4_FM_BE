package backend.mossy.shared.market.dto.toss;

public record TossPaymentResponse(
    String paymentKey,      // 결제 고유 키
    String orderId,         // 우리 쪽 주문 ID
    String orderName,       // 주문명 (예: 토스 티셔츠 외 2건)
    String status,          // 결제 상태 (READY, DONE, CANCELED 등)
    String method,          // 결제 수단 (카드, 가상계좌 등)
    long totalAmount,       // 결제 총 금액
    String requestedAt,     // 결제 요청 시각
    String approvedAt,      // 결제 승인 시각
    TossCard cancel         // 취소 객체 (필요시 상세 구현)
) {
    public record TossCard(
        String company,
        String number,
        String cardType
    ) {}
}