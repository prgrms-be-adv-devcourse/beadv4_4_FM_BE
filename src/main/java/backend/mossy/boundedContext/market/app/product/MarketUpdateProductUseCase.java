package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.product.Category;
import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.event.ProductUpdatedEvent;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.boundedContext.market.out.product.categoryRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketUpdateProductUseCase {
    private final ProductRepository productRepository;
    private final categoryRepository categoryRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void update(Long productId, Long currentSellerId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));


        // 판매자 검증
        product.validateOwner(currentSellerId);

        // 카테고리 정보 조회
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 모든 정보를 포함하여 업데이트
        product.updateInfo(
                category, // 카테고리 추가
                request.name(),
                request.description(),
                request.price(),
                request.weight(),
                request.quantity(),
                request.status(),
                request.imageUrls() // 이미지 목록 추가
        );

        eventPublisher.publish(new ProductUpdatedEvent(product.getId()));
    }
}