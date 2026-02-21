package com.mossy.boundedContext.product.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "PRODUCT_OPTION_GROUP_MASTER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "product_option_group_master_id"))
public class ProductOptionGroupMaster extends BaseIdAndTime {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}
