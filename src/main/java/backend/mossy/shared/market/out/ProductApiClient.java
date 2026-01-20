package backend.mossy.shared.market.out;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.shared.market.dto.response.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductApiClient {
    private final ProductRepository productRepository;

    public boolean exists(Long productId) {
        return productRepository.existsById(productId);
    }

    public List<CartItemResponse> findCartItemsByBuyerId(Long userId) {
        return productRepository.findCartItemsByBuyerId(userId);
    }

    public Map<Long, BigDecimal> getWeights(List<Long> productIds) {
        return productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getWeight));
    }
}