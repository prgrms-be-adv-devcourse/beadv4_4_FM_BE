package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.domain.CatalogDocument;
import com.mossy.boundedContext.product.out.CatalogSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchUseCase {
    private final CatalogSearchRepository catalogSearchRepository;

    public Page<CatalogDocument> findByName(String name, Pageable pageable) {
        return catalogSearchRepository.findByName(name, pageable);
    }
}
