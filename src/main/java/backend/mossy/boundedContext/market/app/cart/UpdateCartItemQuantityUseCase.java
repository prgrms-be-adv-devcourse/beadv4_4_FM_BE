package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.MarketPolicy;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.exception.DomainException;
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

        boolean updated = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.updateItem(request.productId(), request.quantity()))
                .orElse(false);

        if (!updated) {
            throw new DomainException("404", "장바구니에 해당 상품이 없습니다.");
        }
    }
}
