package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.out.cart.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.request.CartItemUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityUseCase {

    private final CartRepository cartRepository;
    private final MarketPolicy marketPolicy;

    public void updateItemQuantity(Long userId, CartItemUpdateRequest request) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.updateItemQuantity(request.productId(), request.quantity(), marketPolicy);
    }
}
