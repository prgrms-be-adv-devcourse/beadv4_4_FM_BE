package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_OPTION_GROUP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_option_group_id"))
public class ProductOptionGroup extends BaseIdAndTime {

    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;

    @Column(name = "product_option_group_master_id", nullable = false)
    private Long masterId;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_option_group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ProductOptionValue> optionValues = new ArrayList<>();

}