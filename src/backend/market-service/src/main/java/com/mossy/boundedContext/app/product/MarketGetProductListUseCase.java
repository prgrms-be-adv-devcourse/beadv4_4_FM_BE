package com.mossy.boundedContext.app.product;

import com.mossy.boundedContext.domain.product.Product;
import com.mossy.boundedContext.out.product.ProductRepository;
import com.mossy.shared.market.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketGetProductListUseCase {
    private final ProductRepository productRepository;

    public Page<Product> getProductList(Pageable pageable) {
        return productRepository.findByStatus(ProductStatus.FOR_SALE, pageable);
    }
}
