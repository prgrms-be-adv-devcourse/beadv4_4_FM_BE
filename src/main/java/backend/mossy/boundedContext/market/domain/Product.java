package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "MARKET_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

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

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "ON_SALE"; // 기본값 설정

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();
}
