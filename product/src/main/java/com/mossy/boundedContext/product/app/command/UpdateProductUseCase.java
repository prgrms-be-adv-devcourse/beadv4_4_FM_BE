package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.app.ProductOptionAssembler;
import com.mossy.boundedContext.product.app.dto.ProductDataForEvent;
import com.mossy.boundedContext.product.app.query.ProductSummaryQueryService;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;
    private final ProductOptionAssembler optionAssembler;

    @Transactional
    public void updateProduct(Long productId, Long currentSellerId, ProductUpdateRequest request) {

        Product product = productRepository.findByIdWithAllDetails(productId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(currentSellerId);

        // 기본 정보 수정
        product.updateBaseInfo(request.basePrice());

        // 어셈블러를 사용하여 아이템 버전 관리(단종 및 생성) 수행
        optionAssembler.configureForUpdate(product, request.productItems());

        eventPublisher.publish(new ProductCatalogSyncEvent(product.getCatalogProductId()));
    }
}