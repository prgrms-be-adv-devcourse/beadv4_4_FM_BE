package backend.mossy.shared.member.dto.request;

import backend.mossy.shared.member.domain.seller.SellerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SellerRequestCreateRequest(
        @NotNull SellerType sellerType,
        @NotBlank String storeName,
        @NotBlank String businessNum,
        @NotBlank String representativeName,
        String contactEmail,
        String contactPhone,
        @NotBlank String address1,
        String address2,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude
) {
}
