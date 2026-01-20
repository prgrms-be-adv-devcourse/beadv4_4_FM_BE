package backend.mossy.boundedContext.market.domain.market;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;

public record MarketPolicy(
        int maxQuantity
) {
    public void validateCartItemQuantity(int quantity) {
        if (quantity > maxQuantity) {
            throw new DomainException(ErrorCode.QUANTITY_LIMIT_EXCEEDED);
        }
    }
}
