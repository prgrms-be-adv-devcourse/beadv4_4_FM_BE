package com.mossy.boundedContext.catalog.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.product.in.internal.dto.response.ReviewProductInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogFacade {
    private final GetReviewCatalogInfoUseCase getReviewCatalogInfoUseCase;

    public CatalogReviewInfoDto getReviewProductInfo(Long catalogId) {
        return getReviewCatalogInfoUseCase.execute(catalogId);
    }
}
