package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductDocument;
import com.mossy.boundedContext.product.out.ProductElasticRepository;
import com.mossy.boundedContext.product.out.ProductRepository;
import com.mossy.shared.market.enums.ProductStatus;
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
        List<Product> products = productRepository.findAllWithCategoryAndImages(ProductStatus.FOR_SALE);

        List<ProductDocument> documents = products.stream()
                .map(ProductDocument::from)
                .toList();

        esRepository.saveAll(documents);
    }
}
