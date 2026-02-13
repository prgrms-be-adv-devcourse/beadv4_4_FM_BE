package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.domain.CatalogDocument;
import com.mossy.boundedContext.product.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.out.CatalogProductRepository;
import com.mossy.boundedContext.product.out.CatalogSearchRepository;
import com.mossy.boundedContext.product.out.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final CatalogProductRepository catalogProductRepository;
    private final ProductRepository productRepository;
    private final CatalogSearchRepository catalogSearchRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void onProductRegistered(ProductRegisteredEvent event) {
        Long catalogId = event.catalogProductId();

        // 1. 카탈로그 기본 정보 조회
        CatalogProduct catalog = catalogProductRepository.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Catalog not found: " + catalogId));

        // 2. ES에 노출할 통계 데이터 계산
        BigDecimal minPrice = productRepository.findMinPriceByCatalogId(catalogId);
        Long sellerCount = productRepository.countSellersByCatalogId(catalogId);

        // Null 방어 로직 (첫 등록 시 등)
        Double finalMinPrice = (minPrice != null) ? minPrice.doubleValue() : 0.0;
        Long finalSellerCount = (sellerCount != null) ? sellerCount : 0L;

        // 3. Document 생성 및 저장 (Upsert 방식)
        CatalogDocument document = CatalogDocument.from(
                catalog,
                finalMinPrice,
                finalSellerCount
        );

        catalogSearchRepository.save(document);
    }
}
