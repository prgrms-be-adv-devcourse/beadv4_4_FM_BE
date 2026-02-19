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

        // 카탈로그에 속한 상품 중 최저가
        BigDecimal minPrice = productRepository.findMinPriceByCatalogId(catalogId);

        // 카탈로그 상품 판매자 수
        Long sellerCount = productRepository.countSellersByCatalogId(catalogId);

        return new ProductDataForEvent(
                productId,
                catalogId,
                (minPrice != null) ? minPrice.doubleValue() : 0.0,
                (sellerCount != null) ? sellerCount : 0L
        );
    }
}