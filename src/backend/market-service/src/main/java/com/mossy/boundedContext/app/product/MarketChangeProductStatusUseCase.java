package com.mossy.boundedContext.app.product;

import com.mossy.boundedContext.domain.product.Product;
import com.mossy.boundedContext.out.product.ProductRepository;
import com.mossy.shared.market.dto.request.ProductStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketChangeProductStatusUseCase {
    private final ProductRepository productRepository;

    @Transactional
    public void changeStatus(Long productId, Long currentSellerId, ProductStatusUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 판매자 검증
        product.validateOwner(currentSellerId);

        product.changeStatus(request.status());
    }
}
