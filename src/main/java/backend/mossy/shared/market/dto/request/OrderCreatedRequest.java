package backend.mossy.shared.market.dto.request;

import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreatedRequest(
        Long cartId,
        String buyerName,
        String address,
        int itemCount,
        List<ProductInfoResponse> items
){ }
