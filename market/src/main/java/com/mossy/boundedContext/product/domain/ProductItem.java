package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARKET_PRODUCT_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "product_items_id"))
public class ProductItem extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Column(name = "sku_code", unique = true, length = 100)
    private String skuCode;

    @Column(name = "option_combination", length = 255)
    private String optionCombination; // 예: "Red/XL"

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "additional_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "productItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionValue> optionValues = new ArrayList<>();

    // === 연관관계 편의 메서드 === //
    protected void assignProduct(Product product) {
        this.product = product;
    }

    public void addOptionValue(ProductOptionValue value) {
        this.optionValues.add(value);
        value.setProductItem(this);
    }

    // 상품 가격
    public BigDecimal getTotalPrice() {
        return this.product.getBasePrice().add(this.additionalPrice);
    }

    // 재고 감소
    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. (현재 재고: " + this.quantity + ")");
        }
        this.quantity -= quantity;
    }

    // 재고 추가
    public void addStock(int quantity) {
        this.quantity += quantity;
    }
}