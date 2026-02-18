package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT")
@SQLRestriction("status != 'DELETED'")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseIdAndTime {

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "catalog_product_id", nullable = false)
    private Long catalogProductId;

    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice; // 해당 판매자의 기준 판매가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Column(nullable = false)
    @Builder.Default
    private Long salesCount = 0L;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ProductItem> productItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ProductOptionGroup> optionGroups = new ArrayList<>();

    // 비즈니스 로직
    public void delete() {
        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void addProductItem(ProductItem item) {
        if (this.productItems == null) {
            this.productItems = new ArrayList<>();
        }
        this.productItems.add(item);
    }

    public void addOptionGroup(ProductOptionGroup optionGroup) {
        if (this.optionGroups == null) {
            this.optionGroups = new ArrayList<>();
        }
        this.optionGroups.add(optionGroup);
    }
}
