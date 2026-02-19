package com.mossy.boundedContext.coupon.app;

import com.mossy.boundedContext.coupon.domain.Coupon;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.out.CouponRepository;
import com.mossy.boundedContext.coupon.out.UserCouponRepository;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DownloadCouponUseCase {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final MarketUserRepository marketUserRepository;

    @Transactional
    public void download(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DomainException(ErrorCode.COUPON_NOT_FOUND));

        MarketUser marketUser = marketUserRepository.getReferenceById(userId);

        if (userCouponRepository.existsByCouponIdAndMarketUserId(couponId, userId)) {
            throw new DomainException(ErrorCode.COUPON_ALREADY_DOWNLOADED);
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .marketUser(marketUser)
                .coupon(coupon)
                .status(UserCouponStatus.UNUSED)
                .expireAt(coupon.getEndAt())
                .build();

        userCouponRepository.save(userCoupon);
    }
}
