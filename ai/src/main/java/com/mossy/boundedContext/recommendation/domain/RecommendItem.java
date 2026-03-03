package com.mossy.boundedContext.recommendation.domain;

import com.mossy.global.jpa.entity.BaseManualIdAndTime;
import com.mossy.shared.product.enums.ProductStatus;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("recommend_item")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecommendItem extends BaseManualIdAndTime {

    @Column("product_id")
    private Long productId;

    // 검색 대상 텍스트 (상품명 + 설명)
    private String content;

    // pgvector 데이터
    @Column("vector_data")
    private String vectorData;

    private BigDecimal price;

    private ProductStatus status;
}