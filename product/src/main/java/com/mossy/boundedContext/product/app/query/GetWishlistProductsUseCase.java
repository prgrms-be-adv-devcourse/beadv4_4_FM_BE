package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.response.WishlistProductResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.boundedContext.product.out.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetWishlistProductsUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper; // Mapper 주입

    @Transactional(readOnly = true)
    public List<WishlistProductResponse> execute(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Product> products = productRepository.findAllById(productIds);

        return productMapper.toWishlistResponses(products);
    }
}
