package com.mossy.shared.market.dto.request;

import com.mossy.shared.market.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        Long sellerId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        ProductStatus status,
        List<MultipartFile> images
) {
}