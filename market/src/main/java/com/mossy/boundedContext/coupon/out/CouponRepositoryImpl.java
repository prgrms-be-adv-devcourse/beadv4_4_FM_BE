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
                                                coupon.endAt))
                                .from(coupon)
                                .where(
                                                coupon.productItemId.eq(productItemId),
                                                coupon.isActive.isTrue(),
                                                coupon.endAt.gt(LocalDateTime.now()),
                                                JPAExpressions.selectOne()
                                                                .from(userCoupon)
                                                                .where(
                                                                                userCoupon.coupon.eq(coupon),
                                                                                userCoupon.marketUser.id.eq(userId))
                                                                .notExists())
                                .fetch();
        }

        @Override
        public CouponStatistics getSellerCouponStatistics(Long sellerId, IssuerType issuerType) {
                LocalDateTime now = LocalDateTime.now();

                return queryFactory
                                .select(Projections.constructor(CouponStatistics.class,
                                                coupon.count(),
                                                // activeCount: 비활성화 안됨 && 기간 내 && 활성 상태
                                                new CaseBuilder()
                                                                .when(coupon.deactivated.isFalse()
                                                                                .and(coupon.endAt.goe(now))
                                                                                .and(coupon.isActive.isTrue()))
                                                                .then(1L)
                                                                .otherwise(0L)
                                                                .sum(),
                                                // inactiveCount: 판매자가 수동 비활성화
                                                new CaseBuilder()
                                                                .when(coupon.deactivated.isTrue()).then(1L)
                                                                .otherwise(0L)
                                                                .sum(),
                                                // expiredCount: 비활성화 안됨 && 기간 만료
                                                new CaseBuilder()
                                                                .when(coupon.deactivated.isFalse()
                                                                                .and(coupon.endAt.lt(now)))
                                                                .then(1L)
                                                                .otherwise(0L)
                                                                .sum()))
                                .from(coupon)
                                .where(
                                                coupon.issuerId.eq(sellerId),
                                                coupon.issuerType.eq(issuerType))
                                .fetchOne();
        }

        @Override
        public List<SellerCouponListResponse> findSellerCouponsContentOnly(Long sellerId, IssuerType issuerType,
                        Pageable pageable) {
                List<com.mossy.boundedContext.coupon.domain.Coupon> coupons = queryFactory
                                .selectFrom(coupon)
                                .where(
                                                coupon.issuerId.eq(sellerId),
                                                coupon.issuerType.eq(issuerType))
                                .orderBy(coupon.createdAt.desc())
                                .offset(pageable.getOffset())
                                .limit(pageable.getPageSize())
                                .fetch();

                return coupons.stream()
                                .map(SellerCouponListResponse::from)
                                .toList();
        }
}
