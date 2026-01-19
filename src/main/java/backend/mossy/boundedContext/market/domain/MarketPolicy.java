package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MarketPolicy {

    @Value("${market.policy.cart.max_quantity}")
    private int maxQuantity;

    public void validateCartItemQuantity(int quantity) {
        if (quantity > maxQuantity) {
            throw new DomainException(ErrorCode.QUANTITY_LIMIT_EXCEEDED);
        }
    }
}