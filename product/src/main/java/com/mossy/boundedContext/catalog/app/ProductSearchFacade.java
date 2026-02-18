package com.mossy.boundedContext.catalog.app;

import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.in.dto.command.ProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductSearchFacade {

    private final ProductSearchUseCase productSearchUseCase;

    // 메인 화면 상품 리스트 (인기순)
    public Page<CatalogDocument> search(ProductSearchCondition condition, Pageable pageable) {
        return productSearchUseCase.search(condition, pageable);
    }
}
