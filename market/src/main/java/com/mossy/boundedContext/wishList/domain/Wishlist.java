package com.mossy.boundedContext.wishList.domain;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_WISHLIST",
        uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "product_id"})
    }
)
@AttributeOverride(name = "id", column = @Column(name = "wishlist_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Wishlist extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketUser marketUser;

    @Column(name = "product_id")
    private Long productId;

    public static Wishlist create(MarketUser marketUser, Long productId) {
        return Wishlist.builder()
                .marketUser(marketUser)
                .productId(productId)
                .build();
    }
}
