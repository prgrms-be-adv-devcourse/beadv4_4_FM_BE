package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.ProductItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_ITEMS")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "product_items_id"))
public class ProductItem extends BaseIdAndTime {

    @Column(name = "product_id", nullable = false, insertable = false, updatable = false)
    private Long productId;

    @Column(name = "sku_code", unique = true, length = 100)
    private String skuCode;

    @Column(name = "option_combination", length = 255)
    private String optionCombination;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "additional_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductItemStatus status = ProductItemStatus.ON_SALE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_items_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ProductOptionValue> optionValues = new ArrayList<>();

    public void addOptionValue(ProductOptionValue value) {
        if (this.optionValues == null) this.optionValues = new ArrayList<>();
        this.optionValues.add(value);
    }

    // 재고 감소
    public void removeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. (현재 재고: " + this.quantity + ")");
        }
        this.quantity -= quantity;
    }

    // 재고 추가
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}