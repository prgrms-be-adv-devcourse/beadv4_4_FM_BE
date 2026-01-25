package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.product.ProductDocument;
import backend.mossy.boundedContext.market.out.product.ProductElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchUseCase {
    private final ProductElasticRepository esRepository;

    public Page<ProductDocument> findByName(String name, Pageable pageable) {
        return esRepository.findByName(name, pageable);
    }
}
