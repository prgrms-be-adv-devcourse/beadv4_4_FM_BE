package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_CART_ITEM")
@NoArgsConstructor
@Getter
public class CartItem extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int count;

    public CartItem(Cart cart, Long productId, int count) {
        this.cart = cart;
        this.productId = productId;
        this.count = count;
    }
}
