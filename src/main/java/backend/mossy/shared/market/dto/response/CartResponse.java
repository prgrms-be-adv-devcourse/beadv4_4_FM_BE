package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.Cart;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        Long buyerId,
        int totalQuantity,
        List<CartItemResponse> items
) {
    public static CartResponse of(Cart cart, List<CartItemResponse> items) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .buyerId(cart.getBuyer().getId())
                .totalQuantity(cart.getTotalQuantity())
                .items(items)
                .build();
    }
}