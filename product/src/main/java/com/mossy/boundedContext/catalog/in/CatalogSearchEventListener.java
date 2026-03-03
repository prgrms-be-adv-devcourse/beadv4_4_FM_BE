package com.mossy.boundedContext.catalog.in;

import com.mossy.boundedContext.catalog.app.dto.CatalogSummaryData;
import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.out.CatalogImageRepository;
import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.boundedContext.catalog.out.CatalogSearchRepository;
import com.mossy.boundedContext.catalog.app.port.CatalogProductQueryPort;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.domain.event.ProductDeletedEvent;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogSearchEventListener {

    private final CatalogProductRepository catalogProductRepository;
    private final CatalogSearchRepository catalogSearchRepository;
    private final CatalogImageRepository catalogImageRepository;
    private final CatalogProductQueryPort catalogProductQueryPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncCatalogSearch(ProductCatalogSyncEvent event) {
        Long catalogId = event.catalogProductId();

        CatalogSummaryData summary = catalogProductQueryPort.getCatalogSummary(catalogId);

        CatalogProduct catalog = catalogProductRepository.findById(catalogId)
                .orElseThrow(() -> new DomainException(ErrorCode.CATALOG_PRODUCT_NOT_FOUND));

//        String thumbnailUrl = catalogImageRepository.findByTargetIdAndIsThumbnailTrue(catalogId)
//                .map(CatalogImage::getImageUrl)
//                .orElse("default_image_url");
        String thumbnailUrl = catalog.getThumbnail();

        CatalogDocument document = CatalogDocument.from(
                catalog,
                thumbnailUrl,
                summary.minPrice().doubleValue(),
                summary.sellerCount(),
                summary.minPriceProductItemId()
        );

        catalogSearchRepository.save(document);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductDeleted(ProductDeletedEvent event) {

        catalogSearchRepository.deleteById(event.productId());

    }
}