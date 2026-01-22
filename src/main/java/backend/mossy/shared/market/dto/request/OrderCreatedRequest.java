package backend.mossy.shared.market.dto.request;

import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderCreatedRequest(
        String buyerAddress,
        BigDecimal buyerLatitude,
        BigDecimal buyerLongitude,
        BigDecimal totalPrice,
        String paymentType,
        List<ProductInfoResponse> items
){ }
