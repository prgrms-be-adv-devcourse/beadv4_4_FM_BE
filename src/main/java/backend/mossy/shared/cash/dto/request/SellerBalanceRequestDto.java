package backend.mossy.shared.cash.dto.request;

import backend.mossy.boundedContext.cash.domain.seller.SellerEventType;
import java.math.BigDecimal;

public record SellerBalanceRequestDto(
    Long sellerId,
    BigDecimal amount,
    SellerEventType eventType,
    String relTypeCode,
    Long relId
) {
    public SellerBalanceRequestDto {
        if (sellerId == null) throw new IllegalArgumentException("판매자 ID는 필수입니다.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }
}