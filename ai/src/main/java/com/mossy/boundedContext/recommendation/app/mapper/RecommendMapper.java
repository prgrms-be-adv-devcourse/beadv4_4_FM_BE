package com.mossy.boundedContext.recommendation.app.mapper;

import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.shared.product.event.ProductCreatedEvent;
import com.mossy.shared.product.event.ProductUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
    componentModel = "spring"
)
public interface RecommendMapper {

    @Mapping(target = "content", source = ".", qualifiedByName = "createContent")
    ProductCreateRequestDto toDto(ProductCreatedEvent event);

    @Named("createContent")
    default String createContent(ProductCreatedEvent event) {
        return buildContent(event.name(), event.categoryName(), event.description(), event.optionGroups());
    }

    default String toContent(ProductUpdatedEvent event) {
        return buildContent(event.name(), event.categoryName(), event.description(), event.optionGroups());
    }

    private String buildContent(String name, String categoryName, String description, List<String> optionGroups) {
        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "상품명", name);
        appendIfPresent(sb, "카테고리", categoryName);

        if (hasText(description)) {
            appendIfPresent(sb, "설명", description.replaceAll("<[^>]*>", ""));
        }

        if (optionGroups != null && !optionGroups.isEmpty()) {
            appendIfPresent(sb, "옵션", String.join("/", optionGroups));
        }

        return sb.toString();
    }

    private void appendIfPresent(StringBuilder sb, String label, Object value) {
        if (value != null && hasText(value.toString())) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(label).append(": ").append(value);
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.isBlank();
    }
}
