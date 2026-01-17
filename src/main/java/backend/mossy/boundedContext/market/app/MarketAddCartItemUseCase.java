package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.CartItemAddRequest;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketAddCartItemUseCase {
    private final CartRepository cartRepository;
    private final ProductApiClient productClient;

    public RsData<Void> addCartItem(Long userId, CartItemAddRequest request) {
        if (!productClient.exists(request.productId())) {
            throw new DomainException("404", "해당 상품이 존재하지 않습니다.");
        }

        Cart cart = cartRepository.findByBuyerIdWithItems(userId).orElseThrow(
                ()-> new DomainException("404", "장바구니가 존재하지 않습니다.")
        );

        cart.addItem(request.productId(), request.quantity());

        return new RsData<>("200", "상품이 장바구니에 추가되었습니다.");
    }
}