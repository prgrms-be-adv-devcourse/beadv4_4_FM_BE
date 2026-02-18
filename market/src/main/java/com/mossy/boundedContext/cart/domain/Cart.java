package com.mossy.boundedContext.cart.domain;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.global.jpa.entity.BaseIdAndTime;
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

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public static Cart createCart(MarketUser buyer) {
        Cart cart = new Cart();
        cart.buyer = buyer;
        return cart;
    }

    public void addItem(Long productItemId, int quantity, MarketPolicy policy) {
        Optional<CartItem> foundItem = findItem(productItemId);

        int totalQuantity = foundItem
            .map(item -> item.getQuantity() + quantity)
            .orElse(quantity);

        policy.validateCartItemQuantity(totalQuantity);

        foundItem.ifPresentOrElse(
            item -> item.addItem(quantity),
            () -> this.items.add(new CartItem(this, productItemId, quantity))
        );
    }

    public void updateItemQuantity(Long productItemId, int quantity, MarketPolicy policy) {
        policy.validateCartItemQuantity(quantity);
        CartItem item = getItem(productItemId);
        item.updateItemQuantity(quantity);
    }

    public void removeItem(Long productId) {
        CartItem item = getItem(productId);
        this.items.remove(item);
    }

    public void removeItems(List<Long> productItemIds) {
        this.items.removeIf(item -> productItemIds.contains(item.getProductItemId()));
    }

    private Optional<CartItem> findItem(Long productItemId) {
        return this.items.stream()
            .filter(item -> item.getProductItemId().equals(productItemId))
            .findFirst();
    }

    private CartItem getItem(Long productItemId) {
        return findItem(productItemId)
            .orElseThrow(() -> new DomainException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    public void clear() {
        this.items.clear();
    }
}
