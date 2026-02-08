package com.mossy.boundedContext.wishList.domain;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.product.domain.Product;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    public static Wishlist create(MarketUser marketUser, Product product) {
        return Wishlist.builder()
                .marketUser(marketUser)
                .product(product)
                .build();
    }
}
