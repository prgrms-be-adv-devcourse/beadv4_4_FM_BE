package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketDecreaseStockUseCase {
    private final ProductRepository productRepository;

    @Transactional
    public void decrease(Long productId, int quantity) {
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.removeStock(quantity);
    }
}