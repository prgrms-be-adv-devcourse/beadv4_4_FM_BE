package com.mossy.boundedContext.catalog.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogProductInfo;
import com.mossy.boundedContext.catalog.app.dto.CatalogProductWithCategoryInfo;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogProductQueryService {

    private final CatalogProductRepository catalogProductRepository;

    @Transactional(readOnly = true)
    public CatalogProductInfo getProductInfo(Long catalogProductId) {
        return catalogProductRepository.findById(catalogProductId)
                .map(CatalogProductInfo::from)
                .orElseThrow(() -> new DomainException(ErrorCode.CATALOG_PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CatalogProductWithCategoryInfo> getProductInfos(List<Long> catalogProductIds) {
        if (catalogProductIds == null || catalogProductIds.isEmpty()) {
            return Collections.emptyList();
        }

        // DB에서 카테고리와 함께 Fetch Join으로 조회해 옴
        List<CatalogProduct> catalogs = catalogProductRepository.findAllByIdWithCategory(catalogProductIds);

        // Entity 리스트를 DTO 리스트로 변환하여 반환
        return catalogs.stream()
                .map(CatalogProductWithCategoryInfo::from)
                .toList();
    }
}
