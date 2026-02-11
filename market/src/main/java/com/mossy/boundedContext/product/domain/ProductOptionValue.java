package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MARKET_PRODUCT_OPTION_VALUE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_option_value_id"))
public class ProductOptionValue extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_items_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem; // 어떤 SKU에 해당하는 값인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_group_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductOptionGroup optionGroup; // 어떤 그룹(색상/사이즈)에 속하는지

    @Column(nullable = false)
    private String value; // 예: "Black", "XL"
}
