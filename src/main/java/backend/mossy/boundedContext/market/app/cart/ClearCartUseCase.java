package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.out.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClearCartUseCase {

    private final CartRepository cartRepository;

    public void clear(Long userId) {
        cartRepository.findByBuyerId(userId).ifPresent(Cart::clear);
    }

}
