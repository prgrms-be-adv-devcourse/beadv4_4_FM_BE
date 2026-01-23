package backend.mossy.boundedContext.market.in.product;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.ProductDocument;
import backend.mossy.boundedContext.market.domain.product.ProductStatus;
import backend.mossy.boundedContext.market.out.product.ProductElasticRepository;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductDataInit {
    private final ProductRepository productRepository;
    private final ProductElasticRepository esRepository;

    @Bean
    public ApplicationRunner productDateInitRunner() {
        return args -> {
            esRepository.deleteAll();
            migrateAll();
        };
    }

    private void migrateAll() {
        List<Product> products = productRepository.findAllWithCategory(ProductStatus.FOR_SALE);

        List<ProductDocument> documents = products.stream()
                .map(ProductDocument::from)
                .toList();

        esRepository.saveAll(documents);
    }
}
