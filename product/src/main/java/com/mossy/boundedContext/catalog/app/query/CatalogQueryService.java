package com.mossy.boundedContext.catalog.app.query;

import com.mossy.boundedContext.catalog.app.dto.CatalogDto;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogQueryService {
    private final CatalogProductRepository catalogProductRepository;

    public CatalogDto getCatalogDto(Long catalogProductId) {
        return catalogProductRepository.findById(catalogProductId)
                .map(CatalogDto::from)
                .orElseThrow(() -> new DomainException(ErrorCode.CATALOG_PRODUCT_NOT_FOUND));
    }
}
