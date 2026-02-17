package com.mossy.boundedContext.coupon.out;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon WHERE uc.id IN :ids")
    List<UserCoupon> findAllWithCouponByIdIn(@Param("ids") List<Long> ids);
}
