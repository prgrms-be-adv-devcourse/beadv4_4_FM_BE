package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.CartItemAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketAddCartItemUseCase {
    private final CartRepository cartRepository;

    public RsData<Void> addCartItem(Long userId, CartItemAddRequest request) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        cart.addItem(request.productId(), request.count());

        return new RsData<>("200", "상품이 장바구니에 추가되었습니다.");
    }
}