package com.mossy.boundedContext.catalog.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogProductInfo;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogProductQueryService {

    private final CatalogProductRepository catalogProductRepository;

    public CatalogProductInfo getProductInfo(Long catalogProductId) {
        return catalogProductRepository.findById(catalogProductId)
                .map(catalog -> new CatalogProductInfo(
                        catalog.getId(),
                        catalog.getName(),
                        catalog.getWeight()
                ))
                .orElseThrow(() -> new DomainException(ErrorCode.CATALOG_PRODUCT_NOT_FOUND));
    }
}
