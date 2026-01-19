package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.MarketPolicy;
import backend.mossy.boundedContext.market.out.CartRepository;
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
        marketPolicy.validateCartItemQuantity(request.quantity());

        boolean isUpdated = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.updateItemQuantity(request.productId(), request.quantity()))
                .orElse(false);

        if (!isUpdated) {
            throw new DomainException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
    }
}
