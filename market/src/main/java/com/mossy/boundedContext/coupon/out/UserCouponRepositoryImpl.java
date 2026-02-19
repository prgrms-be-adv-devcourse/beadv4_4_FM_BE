package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.mossy.boundedContext.coupon.domain.QCoupon.coupon;
import static com.mossy.boundedContext.coupon.domain.QUserCoupon.userCoupon;

@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserCouponResponse> findMyUserCoupons(Long userId) {
        return queryFactory
                .select(Projections.constructor(UserCouponResponse.class,
                        userCoupon.id,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountValue,
                        coupon.maxDiscountAmount,
                        userCoupon.status,
                        userCoupon.expireAt
                ))
                .from(userCoupon)
                .join(userCoupon.coupon, coupon)
                .where(userCoupon.marketUser.id.eq(userId))
                .orderBy(userCoupon.createdAt.desc())
                .fetch();
    }

    @Override
    public List<UserCouponResponse> findApplicableCoupons(Long productItemId, Long userId) {
        return queryFactory
                .select(Projections.constructor(UserCouponResponse.class,
                        userCoupon.id,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountValue,
                        coupon.maxDiscountAmount,
                        userCoupon.status,
                        userCoupon.expireAt
                ))
                .from(userCoupon)
                .join(userCoupon.coupon, coupon)
                .where(
                        userCoupon.marketUser.id.eq(userId),
                        coupon.productItemId.eq(productItemId),
                        userCoupon.status.eq(UserCouponStatus.UNUSED),
                        userCoupon.expireAt.gt(LocalDateTime.now())
                )
                .fetch();
    }
}
