package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserCouponsUseCase {

    private final UserCouponRepository userCouponRepository;

    @Transactional(readOnly = true)
    public Map<Long, UserCoupon> getUserCoupons(List<Long> userCouponIds) {
        if (userCouponIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserCoupon> userCouponMap = userCouponRepository.findByIdIn(userCouponIds).stream()
                .collect(Collectors.toMap(UserCoupon::getId, userCoupon -> userCoupon));

        // 존재하지 않는 쿠폰 검증
        for (Long id : userCouponIds) {
            if (!userCouponMap.containsKey(id)) {
                throw new DomainException(ErrorCode.USER_COUPON_NOT_FOUND);
            }
        }

        return userCouponMap;
    }
}
