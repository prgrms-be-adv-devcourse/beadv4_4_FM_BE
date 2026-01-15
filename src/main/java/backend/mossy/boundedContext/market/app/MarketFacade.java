package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.shared.market.dto.requets.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketGetProductListUseCase marketGetProductListUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;

    @Transactional(readOnly = true)
    public List<Product> getProductList() {
        return marketGetProductListUseCase.getProductList();
    }

    @Transactional
    public Product registerProduct(ProductRequest request) {
        return marketRegisterProductUseCase.register(request);
    }
}
