package com.mossy.boundedContext.product.domain;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARKET_PRODUCT")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionGroup> optionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> productItems = new ArrayList<>();

    // 비즈니스 로직
    public void delete() {
        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    // 판매자 검증
    public void validateOwner(Long sellerId) {
        if (!this.seller.getId().equals(sellerId)) {
            throw new IllegalArgumentException("해당 상품에 대한 권한이 없습니다.");
        }
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    // 연관관계 편의 메서드
    public void addProductItem(ProductItem productItems) {
        this.productItems.add(productItems);
        productItems.assignProduct(this); // ProductItem 쪽에 추가할 메서드
    }

    public void addOptionGroup(ProductOptionGroup group) {
        this.optionGroups.add(group);
        group.setProduct(this);
    }

    public void updateInfo(Category category, @NotBlank(message = "상품명은 필수입니다.") String name, String description, @Positive(message = "가격은 0보다 커야 합니다.") BigDecimal price, BigDecimal weight, Integer quantity, ProductStatus status, Object o) {
    }

    public void removeStock(int quantity) {
    }
}
