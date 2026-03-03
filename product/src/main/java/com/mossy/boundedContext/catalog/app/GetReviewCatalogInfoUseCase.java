package com.mossy.boundedContext.catalog.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetReviewCatalogInfoUseCase {

    private final CatalogProductRepository catalogProductRepository;

    @Transactional(readOnly = true)
    public CatalogReviewInfoDto execute(Long catalogId) {
        CatalogProduct catalogProduct = catalogProductRepository.findById(catalogId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        return new CatalogReviewInfoDto(
                catalogProduct.getName(),
                catalogProduct.getThumbnail(),
                catalogProduct.getReviewCount(),
                catalogProduct.getAverageRating(),
                catalogProduct.getCategory().getName()
        );
    }
}
