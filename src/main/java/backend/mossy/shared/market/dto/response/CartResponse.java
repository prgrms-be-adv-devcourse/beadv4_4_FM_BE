package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        int itemCount,
        List<CartItemResponse> items
) {
    public static CartResponse of(Cart cart, List<CartItemResponse> items) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .itemCount(items.size())
                .items(items)
                .build();
    }
}