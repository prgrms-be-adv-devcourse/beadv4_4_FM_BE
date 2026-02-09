package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
