package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_OPTION_VALUE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_option_value_id"))
public class ProductOptionValue extends BaseIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_items_id", nullable = false,
            insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem;

    @Column(name = "product_option_group_id", nullable = false)
    private Long optionGroupId;

    @Column(nullable = false)
    private String value;

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }
}
