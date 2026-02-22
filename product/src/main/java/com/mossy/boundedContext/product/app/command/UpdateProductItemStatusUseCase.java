package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductItemStatusUseCase {

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public Product  execute(UpdateProductItemStatusCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(command.sellerId());
        product.changeItemStatus(command.productItemId(), command.status());

        eventPublisher.publish(new ProductCatalogSyncEvent(product.getCatalogProductId()));

        return product;
    }
}
