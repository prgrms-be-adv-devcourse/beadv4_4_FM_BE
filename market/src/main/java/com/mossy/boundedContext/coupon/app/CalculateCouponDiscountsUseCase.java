package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.dto.CouponDiscountInfo;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculateCouponDiscountsUseCase {

    private final UserCouponRepository userCouponRepository;

    // 쿠폰이 적용된 상품의 할인가를 계산하는 부분
    @Transactional(readOnly = true)
    public Map<Long, CouponDiscountInfo> calculateDiscounts(Map<Long, BigDecimal> userCouponPriceMap) {
        if (userCouponPriceMap.isEmpty()) {
            return Map.of();
        }

        List<Long> userCouponIds = List.copyOf(userCouponPriceMap.keySet());

        Map<Long, UserCoupon> userCouponMap = userCouponRepository.findByIdIn(userCouponIds).stream()
                .collect(Collectors.toMap(UserCoupon::getId, userCoupon -> userCoupon));

        return userCouponIds.stream()
            .collect(Collectors.toMap(id -> id,
                id -> {
                    UserCoupon userCoupon = userCouponMap.get(id);
                    if (userCoupon == null) {
                        throw new DomainException(ErrorCode.USER_COUPON_NOT_FOUND);
                    }
                    BigDecimal discountAmount = userCoupon.calculateDiscount(userCouponPriceMap.get(id));
                    return new CouponDiscountInfo(discountAmount, userCoupon.getCoupon().getCouponType());
                }
            ));
    }
}
