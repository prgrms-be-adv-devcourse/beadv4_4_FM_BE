package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import com.mossy.boundedContext.coupon.in.dto.response.UserCouponResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.mossy.boundedContext.coupon.domain.QCoupon.coupon;
import static com.mossy.boundedContext.coupon.domain.QUserCoupon.userCoupon;

@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepositoryCustom {

        private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserCouponResponse> findMyUserCoupons(Long userId, UserCouponStatus status, Pageable pageable) {
        List<UserCouponResponse> content = queryFactory
                .select(Projections.constructor(UserCouponResponse.class,
                        userCoupon.id,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountValue,
                        coupon.maxDiscountAmount,
                        userCoupon.status,
                        coupon.endAt
                ))
                .from(userCoupon)
                .join(userCoupon.coupon, coupon)
                .where(
                        userCoupon.marketUser.id.eq(userId),
                        status != null ? userCoupon.status.eq(status) : null
                )
                .orderBy(userCoupon.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

                JPAQuery<Long> countQuery = queryFactory
                                .select(userCoupon.count())
                                .from(userCoupon)
                                .where(
                                        userCoupon.marketUser.id.eq(userId),
                                        status != null ? userCoupon.status.eq(status) : null
                                );

                return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
        }

        @Override
        public List<UserCouponResponse> findApplicableCoupons(Long userId, List<Long> productItemIds) {
                return queryFactory
                                .select(Projections.constructor(UserCouponResponse.class,
                                                userCoupon.id,
                                                coupon.couponName,
                                                coupon.couponType,
                                                coupon.discountValue,
                                                coupon.maxDiscountAmount,
                                                userCoupon.status,
                                                coupon.endAt))
                                .from(userCoupon)
                                .join(userCoupon.coupon, coupon)
                                .where(
                                                userCoupon.marketUser.id.eq(userId),
                                                coupon.productItemId.in(productItemIds),
                                                userCoupon.status.eq(UserCouponStatus.UNUSED),
                                                coupon.endAt.gt(LocalDateTime.now()))
                                .orderBy(
                                                coupon.discountValue.desc(),
                                                coupon.endAt.asc())
                                .fetch();
        }
}
