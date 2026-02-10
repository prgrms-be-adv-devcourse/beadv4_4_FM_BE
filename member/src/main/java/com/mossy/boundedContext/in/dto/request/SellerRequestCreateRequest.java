package com.mossy.boundedContext.in.dto.request;

import com.mossy.shared.member.domain.enums.SellerType;
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
