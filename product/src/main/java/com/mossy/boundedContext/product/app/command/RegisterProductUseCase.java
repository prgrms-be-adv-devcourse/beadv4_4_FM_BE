package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.catalog.app.CatalogProductQueryService;
import com.mossy.boundedContext.catalog.app.dto.CatalogProductInfo;
import com.mossy.boundedContext.product.app.ProductOptionAssembler;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterProductUseCase {
    private final ProductRepository productRepository;
    private final ProductOptionAssembler productOptionAssembler;
    private final CatalogProductQueryService catalogProductQueryService;
    private final EventPublisher eventPublisher;

    @Transactional
    public Long register(ProductCreateRequest request) {

        // 카탈로그 정보
        CatalogProductInfo catalogInfo = catalogProductQueryService.getProductInfo(request.catalogProductId());

        Product product = Product.builder()
                .sellerId(request.sellerId())
                .catalogProductId(request.catalogProductId())
                .basePrice(request.basePrice())
                .status(ProductStatus.FOR_SALE)
                .build();

        productOptionAssembler.configureForCreate(product, catalogInfo, request.optionGroups(), request.productItems());

        eventPublisher.publish(new ProductCatalogSyncEvent(product.getCatalogProductId()));

        return productRepository.save(product).getId();
    }
}
