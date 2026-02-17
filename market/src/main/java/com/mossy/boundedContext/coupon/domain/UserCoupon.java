package com.mossy.boundedContext.coupon.domain;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketUser marketUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Coupon coupon;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus status;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    public void validate() {
        if (this.status != UserCouponStatus.UNUSED) {
            throw new DomainException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (LocalDateTime.now().isAfter(this.expireAt)) {
            throw new DomainException(ErrorCode.COUPON_EXPIRED);
        }
    }

    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        validate();
        return coupon.calculateDiscount(originalPrice);
    }

    public void use() {
        this.status = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
