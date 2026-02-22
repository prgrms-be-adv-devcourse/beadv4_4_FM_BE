package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.catalog.out.CatalogSummaryDto;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.shared.product.enums.ProductItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSummaryQueryService {
    private final ProductRepository productRepository;

    private static final List<ProductItemStatus> ACTIVE_STATUSES = List.of(
            ProductItemStatus.ON_SALE,
            ProductItemStatus.PRE_ORDER,
            ProductItemStatus.OUT_OF_STOCK
    );

    public ProductDataForEvent getCatalogSummary(Long catalogId) {

        CatalogSummaryDto summary = productRepository.findCatalogSummaryByCatalogId(catalogId, ACTIVE_STATUSES)
                .orElse(null);

        return new ProductDataForEvent(
                catalogId,
                (summary != null && summary.getMinPrice() != null) ? summary.getMinPrice() : BigDecimal.ZERO,
                (summary != null) ? summary.getSellerCount() : 0L,
                (summary != null) ? summary.getMinPriceProductId() : null
        );
    }
}