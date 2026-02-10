package com.mossy.boundedContext.coupon.domain;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_USER_COUPON")
@AttributeOverride(name = "id", column = @Column(name = "user_coupon_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class UserCoupon extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MarketUser marketUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;
}
