package com.mossy.boundedContext.product.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "catalogs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CatalogDocument {

    @Id
    private Long id; // CatalogProduct ID

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name; // 상품명

    @Field(type = FieldType.Keyword)
    private String brand; // 브랜드명

    @Field(type = FieldType.Keyword)
    private String modelNumber;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Text)
    private String description; // 상품 설명

    @Field(type = FieldType.Keyword)
    private String thumbnail;

    // 검색 결과 리스트에서 보여줄 요약 정보
    @Field(type = FieldType.Double)
    private Double minPrice; // 최저가

    @Field(type = FieldType.Long)
    private Long sellerCount; // 판매자 수

    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * Entity -> Document 변환 정적 팩토리 메서드
     * minPrice와 sellerCount는 별도의 집계 쿼리 결과를 주입받는 구조를 권장합니다.
     */
    public static CatalogDocument from(CatalogProduct catalog, Double minPrice, Long sellerCount) {
        return CatalogDocument.builder()
                .id(catalog.getId())
                .name(catalog.getName())
                .brand(catalog.getBrand())
                .modelNumber(catalog.getModelNumber())
                .categoryId(catalog.getCategory().getId())
                .categoryName(catalog.getCategory().getName())
                .description(catalog.getDescription())
                .thumbnail(catalog.getThumbnail())
                .minPrice(minPrice)
                .sellerCount(sellerCount)
                .status(catalog.getStatus().name())
                .build();
    }
}
