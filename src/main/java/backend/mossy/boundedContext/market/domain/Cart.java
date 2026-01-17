package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
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
@AttributeOverride(name = "id", column = @Column(name = "cart_id"))
public class Cart extends BaseIdAndTime {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketUser buyer;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_count", nullable = false)
    private int totalQuantity;

    public static Cart createCart(MarketUser buyer) {
        Cart cart = new Cart();
        cart.buyer = buyer;
        cart.totalQuantity = 0;
        return cart;
    }

    public void addItem(Long productId, int quantity){
        for (CartItem item : this.items) {
            if (item.getProductId().equals(productId)) {
                item.addItem(quantity);
                this.totalQuantity += quantity;
                return;
            }
        }
        CartItem cartItem = new CartItem(this, productId, quantity);
        this.getItems().add(cartItem);
        this.totalQuantity += quantity;
    }
}