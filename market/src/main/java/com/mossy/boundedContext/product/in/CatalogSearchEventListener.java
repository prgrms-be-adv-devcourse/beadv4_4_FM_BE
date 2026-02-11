package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.domain.CatalogDocument;
import com.mossy.boundedContext.product.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.in.dto.command.CatalogSummary;
import com.mossy.boundedContext.product.out.CatalogProductRepository;
import com.mossy.boundedContext.product.out.CatalogSearchRepository;
import com.mossy.boundedContext.product.out.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogSearchEventListener {
    
    private final CatalogSearchRepository catalogSearchRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final ProductRepository productRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void productRegisteredEvent(ProductRegisteredEvent event) {
        syncCatalogToElasticsearch(event.productId());
    }

    private void syncCatalogToElasticsearch(Long productId) {
        try {
            Long catalogId = productRepository.findCatalogIdByProductId(productId)
                    .orElseThrow(() -> new EntityNotFoundException("카탈로그 ID를 찾을 수 없습니다. 상품 ID: " + productId));

            CatalogProduct catalog = catalogProductRepository.findByIdWithCategory(catalogId)
                    .orElseThrow(() -> new EntityNotFoundException("카탈로그 정보를 찾을 수 없습니다. ID: " + catalogId));

            CatalogSummary summary = productRepository.getCatalogSummary(catalogId);

            CatalogDocument document = CatalogDocument.from(
                    catalog,
                    summary.minPrice(),
                    summary.sellerCount()
            );

            catalogSearchRepository.save(document);

            log.info("카탈로그 검색 인덱싱 성공 - 카탈로그 ID: {}, 최저가: {}, 판매처: {}",
                    catalogId, summary.minPrice(), summary.sellerCount());

        } catch (EntityNotFoundException e) {
            log.error("카탈로그 검색 인덱싱 실패 - 상품 ID: {}", productId, e);
        }
    }
}
