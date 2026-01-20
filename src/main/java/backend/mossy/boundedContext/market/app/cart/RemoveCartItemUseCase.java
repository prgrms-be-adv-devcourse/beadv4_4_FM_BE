package backend.mossy.boundedContext.market.app.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.out.cart.CartRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderDetailDto;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    public void removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByBuyerId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.CART_NOT_FOUND));

        cart.removeItem(productId);
    }

    public void removeItems(PaymentCompletedEvent event) {
        Long buyerId = event.order().buyerId();
        List<Long> productIds = event.orderDetails().stream()
                .map(OrderDetailDto::productId)
                .toList();

        cartRepository.findByBuyerId(buyerId)
                .ifPresent(cart -> cart.removeItems(productIds));
    }
}
