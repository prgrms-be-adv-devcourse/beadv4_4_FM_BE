package com.mossy.boundedContext.product.domain;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.product.enums.ProductItemStatus;
import com.mossy.shared.product.enums.ProductStatus;
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

    @Column(name = "sku_code", length = 100)
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

    // 아이템 판매 중지
    public void markAsStopped() {
        if (this.status != ProductItemStatus.DELETED && this.status != ProductItemStatus.SUSPENDED)
        this.status = ProductItemStatus.STOPPED;
    }

    // 아이템 판매 정지
    public void markAsSuspended() {
        if (this.status != ProductItemStatus.DELETED)
        this.status = ProductItemStatus.SUSPENDED;
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

    // 재고 차감
    protected void decreaseStock(int amount) {
        // 차감 요청 수량 자체가 유효하지 않은 경우
        if (amount <= 0) {
            throw new DomainException(ErrorCode.INVALID_QUANTITY_REQUEST);
        }

        if (this.quantity < amount) {
            throw new DomainException(ErrorCode.INSUFFICIENT_STOCK);
        }

        this.quantity -= amount;

        if (this.quantity == 0) {
            this.status = ProductItemStatus.OUT_OF_STOCK;
        }
    }

    // 재고 복구
    protected void increaseStock(int amount) {
        // 수량이 0 이하로 들어온 경우 예외 발생
        if (amount <= 0) {
            throw new DomainException(ErrorCode.INVALID_QUANTITY_REQUEST);
        }

        this.quantity += amount;

        if (this.status == ProductItemStatus.OUT_OF_STOCK && this.quantity > 0) {
            this.status = ProductItemStatus.ON_SALE;
        }
    }
}