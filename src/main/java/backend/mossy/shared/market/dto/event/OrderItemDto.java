package backend.mossy.shared.market.dto.event;

import backend.mossy.standard.modelType.HashModelTypeCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long orderId,
        Long buyerId,
        String buyerName,
        Long sellerId,
        String sellerName,
        Long productId,
        String productName,
        BigDecimal price,
        BigDecimal salePrice,
        BigDecimal payoutRate,
        BigDecimal payoutFee,
        BigDecimal salePriceWithoutFee,
        BigDecimal weight,
        BigDecimal deliveryDistance
) implements HashModelTypeCode {

    @Override
    public String getModelTypeCode() {
        return "OrderItem";
    }
}
