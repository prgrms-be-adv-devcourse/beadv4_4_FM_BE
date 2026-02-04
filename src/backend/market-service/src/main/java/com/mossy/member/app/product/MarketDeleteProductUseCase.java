package com.mossy.member.app.product;

import com.mossy.member.domain.product.Product;
import com.mossy.member.domain.product.event.ProductDeletedEvent;
import com.mossy.member.out.product.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketDeleteProductUseCase {
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void delete(Long productId, Long currentSellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.validateOwner(currentSellerId);
        product.delete();

        eventPublisher.publish(new ProductDeletedEvent(productId));
    }
}
