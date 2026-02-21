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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "PRODUCT")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("status != 'DELETED'")
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseIdAndTime {

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "catalog_product_id", nullable = false)
    private Long catalogProductId;

    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Column(nullable = false)
    @Builder.Default
    private Long salesCount = 0L;

    @Column(name = "reject_reason", length = 1000)
    private String rejectReason; // 상품 등록 반려 사유

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

    public void discontinueItem(Long itemId) {
        this.productItems.stream()
                .filter(item -> item.getId().equals(itemId) && item.getStatus() == ProductItemStatus.ON_SALE)
                .findFirst()
                .ifPresent(ProductItem::markAsStopped);
    }

    // 기본 정보 수정
    public void updateBaseInfo(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    // 판매자 확인
    public void validateOwner(Long sellerId) {
        if (!Objects.equals(this.sellerId, sellerId)) {
            throw new DomainException(ErrorCode.PRODUCT_NOT_OWNER);
        }
    }

    public Set<String> getValidOptionValues() {
        return this.productItems.stream()
                .filter(item -> item.getStatus() == ProductItemStatus.ON_SALE ||
                        item.getStatus() == ProductItemStatus.OUT_OF_STOCK)
                .flatMap(item -> item.getOptionValues().stream())
                .map(ProductOptionValue::getValue)
                .collect(Collectors.toSet());
    }

    // 상품 아이템 상태 수정
    public void changeItemStatus(Long itemId, ProductItemStatus newStatus) {
        Set<ProductItemStatus> sellerAllowedStatus = Set.of(
                ProductItemStatus.PRE_ORDER,
                ProductItemStatus.ON_SALE,
                ProductItemStatus.OUT_OF_STOCK,
                ProductItemStatus.STOPPED
        );

        if (!sellerAllowedStatus.contains(newStatus)) {
            throw new DomainException(ErrorCode.PRODUCT_ITEM_INVALID_STATUS);
        }

        this.productItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND))
                .updateStatus(newStatus);
    }

    // 상품 아이템 삭제
    public void deleteItem(Long itemId) {
        this.productItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND))
                .delete();
    }
}
