package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.MarketPolicy;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.boundedContext.market.out.MarketUserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.common.MarketUserDto;
import backend.mossy.shared.market.dto.request.CartItemAddRequest;
import backend.mossy.shared.market.dto.request.CartItemUpdateRequest;
import backend.mossy.shared.market.dto.response.CartItemResponse;
import backend.mossy.shared.market.dto.response.CartResponse;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartUseCase {
    private final CartRepository cartRepository;
    private final MarketUserRepository marketUserRepository;
    private final ProductApiClient productClient;
    private final MarketPolicy marketPolicy;

    public void create(MarketUserDto buyer) {
        MarketUser user = marketUserRepository.getReferenceById(buyer.id());
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }

    public RsData<CartResponse> getCart(Long userId) {
        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException("404", "장바구니가 존재하지 않습니다.")
        );
        List<CartItemResponse> items = cartRepository.findCartItemsByBuyerId(userId);
        return new RsData<>("200", "장바구니 조회 성공", CartResponse.of(cart, items));
    }

    public void addItem(Long userId, CartItemAddRequest request) {
        marketPolicy.validateCartItemQuantity(request.quantity());

        if (!productClient.exists(request.productId())) {
            throw new DomainException("404", "해당 상품이 존재하지 않습니다.");
        }

        Cart cart = cartRepository.findByBuyerId(userId).orElseThrow(
                () -> new DomainException("404", "장바구니가 존재하지 않습니다.")
        );

        cart.addItem(request.productId(), request.quantity());
    }

    public void updateItem(Long userId, CartItemUpdateRequest request) {
        marketPolicy.validateCartItemQuantity(request.quantity());

        boolean updated = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.updateItem(request.productId(), request.quantity()))
                .orElse(false);

        if (!updated) {
            throw new DomainException("404", "장바구니에 해당 상품이 없습니다.");
        }
    }

    public void removeItem(Long userId, Long productId) {
        boolean removed = cartRepository.findByBuyerId(userId)
                .map(cart -> cart.removeItem(productId))
                .orElse(false);

        if (!removed) {
            throw new DomainException("404", "장바구니에 해당 상품이 없습니다.");
        }
    }

    public void clear(Long userId) {
        cartRepository.findByBuyerId(userId).ifPresent(Cart::clear);
    }
}
