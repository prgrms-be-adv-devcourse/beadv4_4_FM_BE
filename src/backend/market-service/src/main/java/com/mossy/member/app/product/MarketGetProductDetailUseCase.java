package com.mossy.member.app.product;

import com.mossy.member.domain.product.Product;
import com.mossy.member.out.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketGetProductDetailUseCase {

    private final ProductRepository productRepository;

    public Product execute(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        return product;
    }
}
