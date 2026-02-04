package com.mossy.member.app.cart;

import com.mossy.member.domain.cart.Cart;
import com.mossy.member.domain.market.MarketUser;
import com.mossy.member.out.cart.CartRepository;
import com.mossy.member.out.market.MarketUserRepository;
import com.mossy.shared.market.dto.event.MarketUserDto;
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
