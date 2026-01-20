package backend.mossy.shared.market.dto.event;

import java.math.BigDecimal;

public record OrderDetailDto (
        Long sellerId,
        Long productId,
        int quantity,                   // 상품의 개수
        BigDecimal orderPrice,          // 상품 가격
        String address                  // 구매자 배송지
){ }
