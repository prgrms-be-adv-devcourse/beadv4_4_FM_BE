package backend.mossy.boundedContext.market.out.product;

import backend.mossy.shared.market.dto.response.CartItemResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<CartItemResponse> findCartItemsByBuyerId(Long buyerId);
}