package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.coupon.domain.UserCouponStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, UserCouponRepositoryCustom {

    @EntityGraph(attributePaths = {"coupon"})
    List<UserCoupon> findByIdIn(List<Long> ids);

    boolean existsByCouponIdAndMarketUserId(Long couponId, Long marketUserId);

    List<UserCoupon> findAllByCouponIdInAndStatus(List<Long> couponIds, UserCouponStatus status);

    void deleteByCouponId(Long couponId);
}
