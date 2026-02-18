package com.mossy.boundedContext.catalog.in;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.out.CatalogImageRepository;
import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.boundedContext.catalog.out.CatalogSearchRepository;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogSearchEventListener {

    private final CatalogProductRepository catalogProductRepository;
    private final CatalogSearchRepository catalogSearchRepository;
    private final CatalogImageRepository catalogImageRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void onProductRegistered(ProductRegisteredEvent event) {
        Long catalogId = event.catalogProductId();

        // 카탈로그 정보 조회
        CatalogProduct catalog = catalogProductRepository.findById(catalogId)
                .orElseThrow(() -> new DomainException(ErrorCode.CATALOG_PRODUCT_NOT_FOUND));

        String thumbnailUrl = catalogImageRepository.findByTargetIdAndIsThumbnailTrue(catalogId)
                .map(CatalogImage::getImageUrl)
                .orElse("default_image_url");

        CatalogDocument document = CatalogDocument.from(
                catalog,
                thumbnailUrl,
                event.minPrice(),
                event.sellerCount()
        );

        catalogSearchRepository.save(document);
    }
}
