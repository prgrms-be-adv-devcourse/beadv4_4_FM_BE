package backend.mossy.shared.market.dto.event;

import backend.mossy.standard.modelType.HashModelTypeCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long customerId,
        String customerName,
        BigDecimal price,
        BigDecimal salePrice,
        LocalDateTime requestPaymentDate,
        LocalDateTime paymentDate
) implements HashModelTypeCode {

    @Override
    public String getModelTypeCode() {
        return "Order";
    }
}

