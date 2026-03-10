package com.mossy.boundedContext.coupon.domain;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_USER_COUPON",
        uniqueConstraints = @UniqueConstraint(columnNames = {"coupon_id", "user_id"}),
        indexes = {
            @Index(name = "idx_user_coupon_user_status_created",
                   columnList = "user_id, status, created_at DESC")
        }
)
@AttributeOverride(name = "id", column = @Column(name = "user_coupon_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class UserCoupon extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MarketUser marketUser;

    @BatchSize(size = 100)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus status;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        validateUsable();
        return coupon.calculateDiscount(originalPrice);
    }

    public void use() {
        this.status = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void restore() {
        if (this.status == UserCouponStatus.EXPIRED) {
            return;
        }

        this.status = UserCouponStatus.UNUSED;
        this.usedAt = null;
    }

    public void expire() {
        this.status = UserCouponStatus.EXPIRED;
        this.expireAt = LocalDateTime.now();
    }

    private void validateUsable() {
        if (this.status == UserCouponStatus.USED) {
            throw new DomainException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (this.status == UserCouponStatus.EXPIRED) {
            throw new DomainException(ErrorCode.COUPON_EXPIRED);
        }
        if (LocalDateTime.now().isAfter(this.expireAt)) {
            throw new DomainException(ErrorCode.COUPON_EXPIRED);
        }
    }
}
