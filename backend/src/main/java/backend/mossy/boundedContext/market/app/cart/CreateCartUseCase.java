package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.out.cart.CartRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.shared.market.dto.event.MarketUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCartUseCase {

    private final MarketUserRepository marketUserRepository;
    private final CartRepository cartRepository;

    public void create(MarketUserDto buyer) {
        MarketUser user = marketUserRepository.getReferenceById(buyer.id());
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }
}
