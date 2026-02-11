package com.mossy.boundedContext.product.in.dto.request;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.product.domain.Category;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.shared.market.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        Long sellerId,
        Long catalogId,
        BigDecimal basePrice,
        List<String> optionGroupNames,
        List<ProductItemRequest> items // 옵션 리스트
) {}