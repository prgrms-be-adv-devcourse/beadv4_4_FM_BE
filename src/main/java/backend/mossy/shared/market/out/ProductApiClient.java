package backend.mossy.shared.market.out;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.response.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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

    public BigDecimal getWeight(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getWeight)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}