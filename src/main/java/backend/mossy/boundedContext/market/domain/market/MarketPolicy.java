package backend.mossy.boundedContext.market.domain.market;

import backend.mossy.boundedContext.market.domain.order.WeightGradeType;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MarketPolicy {

    @Value("${market.policy.cart.max_quantity}")
    private int maxQuantity;

    @Value("${market.policy.weight.small_max}")
    private BigDecimal smallMax;

    @Value("${market.policy.weight.medium_small_max}")
    private BigDecimal mediumSmallMax;

    @Value("${market.policy.weight.medium_max}")
    private BigDecimal mediumMax;

    public void validateCartItemQuantity(int quantity) {
        if (quantity > maxQuantity) {
            throw new DomainException(ErrorCode.QUANTITY_LIMIT_EXCEEDED);
        }
    }

    public WeightGradeType getWeightGrade(BigDecimal totalWeight) {
        if (totalWeight.compareTo(smallMax) < 0) {
            return WeightGradeType.SMALL;
        } else if (totalWeight.compareTo(mediumSmallMax) < 0) {
            return WeightGradeType.MEDIUM_SMALL;
        } else if (totalWeight.compareTo(mediumMax) < 0) {
            return WeightGradeType.MEDIUM;
        } else {
            return WeightGradeType.LARGE;
        }
    }
}
