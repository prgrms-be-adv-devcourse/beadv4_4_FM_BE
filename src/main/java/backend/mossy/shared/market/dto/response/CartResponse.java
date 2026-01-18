package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.Cart;

import java.util.List;

public record CartResponse(
        Long cartId,
        Long buyerId,
        int totalQuantity,
        List<CartItemResponse> items
) {
    public static CartResponse of(Cart cart, List<CartItemResponse> items) {
        return new CartResponse(
                cart.getId(),
                cart.getBuyer().getId(),
                cart.getTotalQuantity(),
                items
        );
    }
}