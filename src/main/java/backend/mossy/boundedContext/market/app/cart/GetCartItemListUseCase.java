package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.out.cart.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.response.CartItemResponse;
import backend.mossy.shared.market.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCartItemListUseCase {

    private final CartRepository cartRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        List<CartItemResponse> items = cartRepository.findCartItemsByBuyerId(userId);

        return CartResponse.of(cart, items);
    }
}
