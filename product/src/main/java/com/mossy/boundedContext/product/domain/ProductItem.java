package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.product.enums.ProductItemStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_ITEMS")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("status != 'DELETED'")
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

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "reject_reason", length = 1000)
    private String rejectReason; // 상품 옵션 반려 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductItemStatus status = ProductItemStatus.ON_SALE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_items_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Builder.Default
    private List<ProductOptionValue> optionValues = new ArrayList<>();


    // 비즈니스 로직
    public void addOptionValue(ProductOptionValue optionValue, ProductOptionGroup group) {
        optionValues.add(optionValue);
    }

    // 상품 아이템 정지
    public void markAsStopped() {
        this.status = ProductItemStatus.STOPPED;
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

    // 상품 아이템 상태 번경
    public void updateStatus(ProductItemStatus status) {
        this.status = status;
    }

    // 삭제
    public void delete() {
        this.status = ProductItemStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}