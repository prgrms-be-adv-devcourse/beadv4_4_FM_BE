package com.mossy.boundedContext.recommendation.app.mapper;

import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.shared.market.event.ProductCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring"
)
public interface RecommendMapper {

    @Mapping(target = "content", source = ".", qualifiedByName = "createContent")
    ProductCreateRequestDto toDto(ProductCreatedEvent event);

    @Named("createContent")
    default String createContent(ProductCreatedEvent event) {
        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "상품명", event.name());
        appendIfPresent(sb, "카테고리", event.categoryName());
        appendIfPresent(sb, "가격", event.price()); // 객체(BigDecimal)도 처리 가능

        if (hasText(event.description())) {
            appendIfPresent(sb, "설명", event.description().replaceAll("<[^>]*>", ""));
        }

        if (event.optionGroups() != null && !event.optionGroups().isEmpty()) {
            appendIfPresent(sb, "옵션", String.join("/", event.optionGroups()));
        }

        return sb.toString();
    }

    default void appendIfPresent(StringBuilder sb, String label, Object value) {
        if (value != null && hasText(value.toString())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(label).append(": ").append(value);
        }
    }

    default boolean hasText(String str) {
        return str != null && !str.isBlank();
    }
}
