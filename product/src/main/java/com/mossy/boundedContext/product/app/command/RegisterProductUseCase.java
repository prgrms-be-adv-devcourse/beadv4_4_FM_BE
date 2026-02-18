package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.shared.market.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RegisterProductUseCase {
    private final ProductRepository productRepository;

    public Product create(Long sellerId, Long catalogId, BigDecimal basePrice) {
        return Product.builder()
                .sellerId(sellerId)
                .catalogProductId(catalogId)
                .basePrice(basePrice)
                .status(ProductStatus.FOR_SALE)
                .build();
    }

    @Transactional
    public Long save(Product product) {
        return productRepository.save(product).getId();
    }
}
