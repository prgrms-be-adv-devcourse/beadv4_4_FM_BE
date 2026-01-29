package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.out.cart.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
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
        if (!productApiClient.exists(request.productId())) {
            throw new DomainException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.addItem(request.productId(), request.quantity(), marketPolicy);
    }
}
