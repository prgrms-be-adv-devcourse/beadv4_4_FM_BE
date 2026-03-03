package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.app.GetReviewCatalogInfoUseCase;
import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.catalog.app.dto.CatalogSummaryData;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.catalog.app.port.CatalogProductQueryPort;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogProductQueryAdapter implements CatalogProductQueryPort {

    private final ProductSummaryQueryService productSummaryQueryService;
    private final GetReviewCatalogInfoUseCase getReviewCatalogInfoUseCase;

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

    @Override
    public Map<Long, CatalogReviewInfoDto> getReviewInfos(List<Long> catalogIds) {
        return catalogIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> getReviewCatalogInfoUseCase.execute(id) // 기존 유스케이스 활용
                ));
    }
}