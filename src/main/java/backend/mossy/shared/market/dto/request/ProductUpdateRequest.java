package backend.mossy.shared.market.dto.request;

import backend.mossy.boundedContext.market.domain.product.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(
        @NotNull(message = "카테고리는 필수입니다.") Long categoryId,
        @NotBlank(message = "상품명은 필수입니다.") String name,
        @Positive(message = "가격은 0보다 커야 합니다.") BigDecimal price,
        String description,
        BigDecimal weight,
        Integer quantity,
        ProductStatus status,
        List<String> imageUrls
) {

}