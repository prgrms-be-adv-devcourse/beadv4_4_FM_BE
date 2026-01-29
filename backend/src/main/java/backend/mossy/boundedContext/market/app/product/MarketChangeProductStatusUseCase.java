package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.shared.market.dto.request.ProductStatusUpdateRequest;
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
