package backend.mossy.shared.market.dto.request;

import backend.mossy.boundedContext.market.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdateRequest(
        @NotNull(message = "변경할 상태값은 필수입니다.")
        ProductStatus status
) {
}
