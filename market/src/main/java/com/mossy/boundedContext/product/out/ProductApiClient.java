package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Long, BigDecimal> getWeights(List<Long> productIds) {
        List<Product> products = productRepository.findByIdIn(productIds);
        Map<Long, BigDecimal> weightMap = new HashMap<>();
        for (Product product : products) {
            weightMap.put(product.getId(), product.getWeight());
        }
        return weightMap;
    }
}