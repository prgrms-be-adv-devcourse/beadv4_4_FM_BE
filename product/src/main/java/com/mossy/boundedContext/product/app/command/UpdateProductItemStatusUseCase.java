package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductItemStatusUseCase {

    private final ProductRepository productRepository;

    public Product  execute(UpdateProductItemStatusCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(command.sellerId());
        product.changeItemStatus(command.productItemId(), command.status());

        return product;
    }
}
