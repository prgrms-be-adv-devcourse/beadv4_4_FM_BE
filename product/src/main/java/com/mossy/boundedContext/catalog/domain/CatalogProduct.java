package com.mossy.boundedContext.catalog.domain;

import com.mossy.boundedContext.category.domain.Category;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.product.enums.CatalogStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CATALOG_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AttributeOverride(name = "id", column = @Column(name = "catalog_product_id"))
public class CatalogProduct extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(length = 100)
    private String modelNumber;

    @Column(length = 2048)
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CatalogStatus status = CatalogStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private Long salesCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double totalRatingSum = 0.0;

    // 카탈로그 정보 수정
    public void updateCatalog(Category category, String name, String brand,
                              String description, BigDecimal weight, String modelNumber) {
        this.category = category;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.weight = weight;
        this.modelNumber = modelNumber;
    }

    public Double getAverageRating() {
        if (reviewCount == 0) return 0.0;
        return totalRatingSum / reviewCount;
    }
}