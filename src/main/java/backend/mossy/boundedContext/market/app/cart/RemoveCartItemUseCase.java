package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    public void removeItem(Long userId, Long productId) {
        boolean removed = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.removeItem(productId))
                .orElse(false);

        if (!removed) {
            throw new DomainException("404", "장바구니에 해당 상품이 없습니다.");
        }
    }
}
