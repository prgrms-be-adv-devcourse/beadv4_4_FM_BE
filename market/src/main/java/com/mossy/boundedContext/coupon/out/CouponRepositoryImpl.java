package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.in.dto.response.CouponResponse;
import com.mossy.boundedContext.coupon.in.dto.response.SellerCouponListResponse;
import com.mossy.boundedContext.coupon.out.dto.CouponStatistics;
import com.mossy.shared.market.enums.IssuerType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

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

    @Override
    public CouponStatistics getSellerCouponStatistics(Long sellerId, IssuerType issuerType) {
        return queryFactory
                .select(Projections.constructor(CouponStatistics.class,
                        coupon.count(),
                        new CaseBuilder()
                                .when(coupon.isActive.isTrue()).then(1L)
                                .otherwise(0L)
                                .sum(),
                        new CaseBuilder()
                                .when(coupon.isActive.isFalse()).then(1L)
                                .otherwise(0L)
                                .sum()
                ))
                .from(coupon)
                .where(
                        coupon.issuerId.eq(sellerId),
                        coupon.issuerType.eq(issuerType)
                )
                .fetchOne();
    }

    @Override
    public List<SellerCouponListResponse> findSellerCouponsContentOnly(Long sellerId, IssuerType issuerType, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(SellerCouponListResponse.class,
                        coupon.id,
                        coupon.productItemId,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountValue,
                        coupon.maxDiscountAmount,
                        coupon.startAt,
                        coupon.endAt,
                        coupon.isActive,
                        coupon.createdAt
                ))
                .from(coupon)
                .where(
                        coupon.issuerId.eq(sellerId),
                        coupon.issuerType.eq(issuerType)
                )
                .orderBy(coupon.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
