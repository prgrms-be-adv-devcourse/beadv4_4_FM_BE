package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductApiClient {
    private final ProductRepository productRepository;

    public boolean exists(Long productId) {
        return productRepository.existsById(productId);
    }

    public List<ProductInfoResponse> findCartItemsByBuyerId(Long userId) {
        return productRepository.findCartItemsByBuyerId(userId);
    }

    public void validateProductOwner(Long productId, Long sellerId) {
        // TODO: 판매자의 상품인지 검증 로직 필요
    }
}