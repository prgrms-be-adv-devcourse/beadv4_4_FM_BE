package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketGetProductListUseCase marketGetProductListUseCase;

    @Transactional(readOnly = true)
    public List<Product> getProductList() {
        return marketGetProductListUseCase.getProductList();
    }
}
