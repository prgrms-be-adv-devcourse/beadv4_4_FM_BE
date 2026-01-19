package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.domain.ProductStatus;
import backend.mossy.boundedContext.market.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketGetProductListUseCase {
    private final ProductRepository productRepository;

    public List<Product> getProductList() {
        return productRepository.findTop10ByStatusOrderByCreatedAtDesc(ProductStatus.FOR_SALE);
    }
}
