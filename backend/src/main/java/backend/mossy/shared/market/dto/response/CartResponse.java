package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        String buyerName,
        String buyerAddress,
        int itemCount,
        List<ProductInfoResponse> items
) {
    public static CartResponse of(
            Cart cart,
            List<ProductInfoResponse> items
    ) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .buyerName(cart.getBuyer().getName())
                .buyerAddress(cart.getBuyer().getAddress())
                .itemCount(items.size())
                .items(items)
                .build();
    }
}