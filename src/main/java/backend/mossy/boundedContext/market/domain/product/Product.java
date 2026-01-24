package backend.mossy.boundedContext.market.domain.product;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.access.AccessDeniedException;

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
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    // 상품 정보 수정
    public void updateInfo(Category category, String name, String description,
                           BigDecimal price, BigDecimal weight, Integer quantity,
                           ProductStatus status, List<String> imageUrls) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.quantity = quantity;
        this.status = status;

        if (imageUrls != null && !imageUrls.isEmpty()) {
            updateImages(imageUrls);
        }
    }

    // 상품 상태 변경 (판매자가 직접 변경)
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    // 삭제 처리
    public void delete() {
        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    // 재고 감소(결제 시 사용)
    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다.");
        }

        int restStock = this.quantity - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다. (현재 재고: " + this.quantity + ")");
        }
        this.quantity = restStock;

        // 재고가 0이 되면 품절 상태로 변경
        if (this.quantity == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    // 판매자 검증
    public void validateOwner(Long sellerId) {
        if (!this.seller.getId().equals(sellerId)) {
            throw new AccessDeniedException("해당 상품에 대한 권한이 없습니다.");
        }
    }

    // 상품 이미지 등록
    public void addImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage image = ProductImage.builder()
                    .imageUrl(imageUrls.get(i))
                    .isThumbnail(i == 0) // 첫 번째 이미지를 썸네일로 설정
                    .product(this)
                    .build();
            this.images.add(image);
        }
    }

    // 상품 이미지 수정
    public List<String> updateImages(List<String> newImageUrls) {
        // 기존 이미지 리스트
        List<String> oldImageUrls = this.images.stream()
                .map(ProductImage::getImageUrl)
                .toList();

        // 기존 이미지 제거
        this.images.clear();

        // 새 이미지 추가
        addImages(newImageUrls);

        // S3 이미지 리스트 반환
        return oldImageUrls;
    }
}
