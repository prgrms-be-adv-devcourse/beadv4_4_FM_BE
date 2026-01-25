package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.event.ProductDeletedEvent;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
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
