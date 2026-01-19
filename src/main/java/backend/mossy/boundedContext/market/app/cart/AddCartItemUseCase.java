package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.MarketPolicy;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.market.dto.request.CartItemAddRequest;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final MarketPolicy marketPolicy;
    private final CartRepository cartRepository;
    private final ProductApiClient productApiClient;

    public void addItem(Long userId, CartItemAddRequest request) {
        marketPolicy.validateCartItemQuantity(request.quantity());

        if (!productApiClient.exists(request.productId())) {
            throw new DomainException("404", "해당 상품이 존재하지 않습니다.");
        }

        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException("404", "장바구니가 존재하지 않습니다.")
        );

        cart.addItem(request.productId(), request.quantity());
    }
}
