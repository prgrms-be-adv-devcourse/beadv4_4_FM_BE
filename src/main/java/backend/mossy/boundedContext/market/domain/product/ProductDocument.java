package backend.mossy.boundedContext.market.domain.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Document(indexName = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Builder
@Setting(settingPath = "elasticsearch/settings.json")
public class ProductDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long productId;

    @Field(type = FieldType.Long)
    private Long sellerId;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Text, analyzer = "mossy_nori_analyzer")
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String thumbnailImage;

    @Field(type = FieldType.Date)
    private OffsetDateTime  createdAt;

    @Field(type = FieldType.Date)
    private OffsetDateTime  updatedAt;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice().doubleValue())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSeller().getId())
                .status(product.getStatus().name())
                .createdAt(product.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(product.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .build();
    }
}
