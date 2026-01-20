package backend.mossy.boundedContext.market.out.cart;

import backend.mossy.shared.market.dto.response.CartItemResponse;

import java.util.List;

public interface CartRepositoryCustom {
    List<CartItemResponse> findCartItemsByBuyerId(Long buyerId);
}