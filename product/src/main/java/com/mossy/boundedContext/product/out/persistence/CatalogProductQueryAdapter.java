package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.app.dto.CatalogSummaryData;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.catalog.app.port.CatalogProductQueryPort;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogProductQueryAdapter implements CatalogProductQueryPort {

    private final ProductSummaryQueryService productSummaryQueryService;

    @Override
    public CatalogSummaryData getCatalogSummary(Long catalogId) {
        ProductDataForEvent data = productSummaryQueryService.getCatalogSummary(catalogId);

        return new CatalogSummaryData(
                data.catalogId(),
                data.minPrice(),
                data.sellerCount(),
                data.minPriceProductItemId()
        );
    }
}