package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductSummaryQueryService {
    private final ProductRepository productRepository;

    public ProductDataForEvent getCatalogSummary(Long catalogId, Long productId) {

        BigDecimal minPrice = productRepository.findMinPriceByCatalogId(catalogId);
        Long sellerCount = productRepository.countSellersByCatalogId(catalogId);

        return new ProductDataForEvent(
                productId,
                catalogId,
                (minPrice != null) ? minPrice.doubleValue() : 0.0,
                (sellerCount != null) ? sellerCount : 0L
        );
    }
}