package backend.mossy.boundedContext.market.domain.cart;

import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(
    name = "MARKET_CART",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_id", columnNames = "user_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "cart_id"))
public class Cart extends BaseIdAndTime {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketUser buyer;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public static Cart createCart(MarketUser buyer) {
        Cart cart = new Cart();
        cart.buyer = buyer;
        return cart;
    }

    public void addItem(Long productId, int quantity, MarketPolicy policy) {
        Optional<CartItem> foundItem = findItem(productId);

        int totalQuantity = foundItem
            .map(item -> item.getQuantity() + quantity)
            .orElse(quantity);

        policy.validateCartItemQuantity(totalQuantity);

        foundItem.ifPresentOrElse(
            item -> item.addItem(quantity),
            () -> this.items.add(new CartItem(this, productId, quantity))
        );
    }

    public void updateItemQuantity(Long productId, int quantity, MarketPolicy policy) {
        policy.validateCartItemQuantity(quantity);
        CartItem item = getItem(productId);
        item.updateItemQuantity(quantity);
    }

    public void removeItem(Long productId) {
        CartItem item = getItem(productId);
        this.items.remove(item);
    }

    private Optional<CartItem> findItem(Long productId) {
        return this.items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }

    private CartItem getItem(Long productId) {
        return findItem(productId)
            .orElseThrow(() -> new DomainException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    public void clear() {
        this.items.clear();
    }
}