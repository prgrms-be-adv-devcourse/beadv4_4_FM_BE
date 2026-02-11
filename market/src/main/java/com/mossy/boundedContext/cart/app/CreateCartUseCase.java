package com.mossy.boundedContext.cart.app;

import com.mossy.boundedContext.cart.domain.Cart;
import com.mossy.boundedContext.cart.out.CartRepository;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.boundedContext.marketUser.in.dto.command.MarketUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCartUseCase {

    private final MarketUserRepository marketUserRepository;
    private final CartRepository cartRepository;

    @Transactional
    public void create(MarketUserDto buyer) {
        MarketUser user = marketUserRepository.getReferenceById(buyer.id());
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }
}
