package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    public void removeItem(Long userId, Long productId) {
        boolean isRemoved = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.removeItem(productId))
                .orElse(false);

        if (!isRemoved) {
            throw new DomainException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
    }
}
