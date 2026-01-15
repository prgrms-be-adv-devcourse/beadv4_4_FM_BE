package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseManualIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARKET_CART")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart extends BaseManualIdAndTime {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MarketUser buyer;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    public static Cart createCart(MarketUser buyer) {
        Cart cart = new Cart();
        cart.buyer = buyer;
        cart.totalCount = 0;
        return cart;
    }

    public void addItem(Long productId, int count){
        CartItem cartItem = new CartItem(this, productId, count);
        this.getItems().add(cartItem);
        this.totalCount += count;
    }
}