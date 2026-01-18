package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.exception.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MarketPolicy {

    @Value("${market.policy.cart.max_quantity}")
    private int maxQuantity;

    public void validateCartItemQuantity(int quantity) {
        if (quantity > maxQuantity) {
            throw new DomainException("422", "수량은 최대 " + maxQuantity + "개까지 가능합니다.");
        }
    }
}