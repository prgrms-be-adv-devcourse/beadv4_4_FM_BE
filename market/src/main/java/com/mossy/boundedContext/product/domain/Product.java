package com.mossy.boundedContext.product.domain;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARKET_PRODUCT")
@SQLRestriction("status != 'DELETED'")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CatalogProduct catalogProduct;

    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice; // 해당 판매자의 기준 판매가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> items = new ArrayList<>();

    // 비즈니스 로직
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public void delete() {
        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    // 연관관계 편의 메서드
    public void addProductItem(ProductItem item) {
        this.items.add(item);
        item.assignProduct(this); // ProductItem 쪽에 추가할 메서드
    }
}
