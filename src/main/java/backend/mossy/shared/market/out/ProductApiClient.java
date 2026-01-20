package backend.mossy.shared.market.out;

import backend.mossy.boundedContext.market.out.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductApiClient {
    private final ProductRepository productRepository;

    public boolean exists(Long productId) {
        return productRepository.existsById(productId);
    }
}