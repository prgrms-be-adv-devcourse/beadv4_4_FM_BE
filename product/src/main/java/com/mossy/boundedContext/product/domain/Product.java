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
import java.util.*;
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

    /* --- 비즈니스 정책 상수 --- */
    // 판매자가 변경 가능한 상품 상태
    private static final Set<ProductStatus> SELLER_ALLOWED_PRODUCT_STATUSES = EnumSet.of(
            ProductStatus.FOR_SALE,
            ProductStatus.PRE_ORDER,
            ProductStatus.OUT_OF_STOCK,
            ProductStatus.DISCONTINUED,
            ProductStatus.HIDDEN
    );

    // 판매자가 변경 가능한 아이템 상태
    private static final Set<ProductItemStatus> SELLER_ALLOWED_ITEM_STATUSES = EnumSet.of(
            ProductItemStatus.PRE_ORDER,
            ProductItemStatus.ON_SALE,
            ProductItemStatus.OUT_OF_STOCK,
            ProductItemStatus.STOPPED
    );

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


    /* --- 비즈니스 로직 --- */

    // 상품 삭제
    public void delete() {
        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();

        // 2. 하위 아이템들도 일괄 삭제 처리 (상태 전파)
        if (this.productItems != null) {
            this.productItems.forEach(ProductItem::delete);
        }
    }

    public void addProductItem(ProductItem item) {
        this.productItems.add(item);
    }

    public void addOptionGroup(ProductOptionGroup optionGroup) {
        this.optionGroups.add(optionGroup);
    }

    public ProductItem findItem(Long itemId) {
        return this.productItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));
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

        validateItemStatusAssignableBySeller(newStatus);

        this.productItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND))
                .updateStatus(newStatus);
    }

    // 아이템 상태 검증
    private void validateItemStatusAssignableBySeller(ProductItemStatus newStatus) {
        if (!SELLER_ALLOWED_ITEM_STATUSES.contains(newStatus)) {
            throw new DomainException(ErrorCode.PRODUCT_ITEM_INVALID_STATUS);
        }
    }

    // 상품 아이템 삭제
    public void deleteItem(Long itemId) {
        this.productItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND))
                .delete();
    }

    // 판매자 상품 상태 변경
    public void updateStatusBySeller(ProductStatus newStatus) {
        validateSellerAssignableStatus(newStatus);
        // 관리자 조치 상태 확인
        if (this.status == ProductStatus.SUSPENDED || this.status == ProductStatus.REJECTED) {
            throw new DomainException(ErrorCode.CANNOT_CHANGE_SUSPENDED_PRODUCT);
        }

        this.status = newStatus;

        // 판매 중단 시킬 때
        if (newStatus == ProductStatus.DISCONTINUED) {
            syncItemsToStoppedStatus();
        }
    }

    // 상품 상태 검증
    private void validateSellerAssignableStatus(ProductStatus status) {
        if (!SELLER_ALLOWED_PRODUCT_STATUSES.contains(status)) {
            throw new DomainException(ErrorCode.INVALID_STATUS_CHANGE_REQUEST);
        }
    }

    private void syncItemsToStoppedStatus() {
        this.productItems.forEach(ProductItem::markAsStopped);
    }

    // 아이템 재고 감소
    public void decreaseItemStock(Long productItemId, int quantity) {
        ProductItem targetItem = this.productItems.stream()
                .filter(item -> item.getId().equals(productItemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND));

        targetItem.decreaseStock(quantity);

        if (this.status == ProductStatus.FOR_SALE && isAllOutOfStock()) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    // 아이템 재고 복구
    public void increaseItemStock(Long productItemId, int quantity) {
        ProductItem targetItem = this.productItems.stream()
                .filter(item -> item.getId().equals(productItemId))
                .findFirst()
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND));

        targetItem.increaseStock(quantity);

        if (this.status == ProductStatus.OUT_OF_STOCK
                && targetItem.getQuantity() > 0) {
            this.status = ProductStatus.FOR_SALE;
        }
    }

    public boolean hasItem(Long productItemId) {
        if (productItemId == null) return false;

        return this.productItems.stream()
                .anyMatch(item -> item.getId().equals(productItemId));
    }

    // 상품 아이템 재고 확인
    private boolean isAllOutOfStock() {
        if (this.productItems == null || this.productItems.isEmpty()) {
            return true;
        }
        return this.productItems.stream()
                .allMatch(item -> item.getQuantity() <= 0);
    }
}
