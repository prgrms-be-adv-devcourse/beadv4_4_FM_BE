package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.boundedContext.market.out.MarketUserRepository;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.MarketUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketCreateCartUseCase {
    private final CartRepository cartRepository;
    private final MarketUserRepository marketUserRepository;

    public RsData<Cart> createCart(MarketUserDto buyer) {
        MarketUser user = marketUserRepository.getReferenceById(buyer.id());

        Cart cart = Cart.createCart(user);

        cartRepository.save(cart);

        return new RsData<>("201", "장바구니가 생성되었습니다.", cart);
    }
}
