package com.mossy.boundedContext.product.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Document(indexName = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ProductDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long productId; // 판매자 등록 상품 ID

    @Field(type = FieldType.Long)
    private Long catalogProductId; // 카탈로그 ID

    @Field(type = FieldType.Long)
    private Long sellerId;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name; // 카탈로그의 상품명

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private Double basePrice; // 판매자가 설정한 가격

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String thumbnailImage;

    // 검색 편의를 위한 옵션 정보 통합 필드
    // 예: ["블랙", "128GB", "SKU-101"]
    @Field(type = FieldType.Text)
    private List<String> options;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime updatedAt;

    // Static Factory Method에서 변환 시 Catalog 정보를 채워줌
    public static ProductDocument from(Product product) {
        CatalogProduct catalog = product.getCatalogProduct();

        // 옵션명들만 리스트로 추출 (검색용)
        List<String> optionNames = product.getProductItems().stream()
                .map(ProductItem::getOptionCombination)
                .toList();

        return ProductDocument.builder()
                .productId(product.getId())
                .catalogProductId(catalog.getId())
                .sellerId(product.getSeller().getId())
                .categoryId(catalog.getCategory().getId())
                .categoryName(catalog.getCategory().getName())
                .name(catalog.getName()) // 이제 이름은 카탈로그에서 가져옴
                .description(catalog.getDescription())
                .basePrice(product.getBasePrice().doubleValue())
                .status(product.getStatus().name())
                .options(optionNames)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
