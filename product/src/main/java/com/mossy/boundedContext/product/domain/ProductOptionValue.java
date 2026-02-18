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
    @JoinColumn(name = "product_option_group_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductOptionGroup optionGroup;

    @Column(nullable = false)
    private String value;

    // 비즈니스 로직
    public void setOptionGroup(ProductOptionGroup group) {
        this.optionGroup = group;
    }

}
