package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {

    @Query("SELECT c FROM Coupon c WHERE c.isActive = false AND c.deactivated = false AND c.startAt <= :now")
    List<Coupon> findActivatableCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.endAt <= :now")
    List<Coupon> findExpiredCoupons(@Param("now") LocalDateTime now);
}
