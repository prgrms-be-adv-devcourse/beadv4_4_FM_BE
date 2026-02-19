package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.mossy.boundedContext.coupon.domain.QCoupon.coupon;
import static com.mossy.boundedContext.coupon.domain.QUserCoupon.userCoupon;

@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CouponResponse> findDownloadableCoupons(Long productItemId, Long userId) {
        return queryFactory
                .select(Projections.constructor(CouponResponse.class,
                        coupon.id,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountValue,
                        coupon.maxDiscountAmount,
                        coupon.endAt
                ))
                .from(coupon)
                .where(
                        coupon.productItemId.eq(productItemId),
                        coupon.isActive.isTrue(),
                        coupon.endAt.gt(LocalDateTime.now()),
                        JPAExpressions.selectOne()
                                .from(userCoupon)
                                .where(
                                        userCoupon.coupon.eq(coupon),
                                        userCoupon.marketUser.id.eq(userId)
                                )
                                .notExists()
                )
                .fetch();
    }
}
