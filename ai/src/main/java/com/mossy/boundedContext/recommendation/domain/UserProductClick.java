package com.mossy.boundedContext.recommendation.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_product_click")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProductClick {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("product_id")
    private Long productId;

    @Column("click_count")
    private Long clickCount;
}

