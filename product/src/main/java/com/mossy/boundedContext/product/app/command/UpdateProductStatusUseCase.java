package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.event.ProductCatalogSyncEvent;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductStatusUseCase {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(UpdateProductStatusCommand command) {

        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(command.sellerId());

        product.updateStatusBySeller(command.newStatus());

        eventPublisher.publishEvent(new ProductCatalogSyncEvent(product.getCatalogProductId()));
    }
}
