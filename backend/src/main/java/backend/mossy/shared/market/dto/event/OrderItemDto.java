package backend.mossy.shared.market.dto.event;

import backend.mossy.standard.modelType.HashModelTypeCode;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long orderId,
        Long buyerId,
        Long sellerId,
        Long productId,
        int quantity,
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