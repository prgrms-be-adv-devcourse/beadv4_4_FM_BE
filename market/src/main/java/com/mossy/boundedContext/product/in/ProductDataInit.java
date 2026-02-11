package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductDocument;
import com.mossy.boundedContext.product.out.CatalogProductRepository;
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
    private final CatalogProductRepository catalogProductRepository;

    @Bean
    public ApplicationRunner productDateInitRunner() {
        return args -> {
            catalogProductRepository.deleteAll();
            migrateAll();
        };
    }

    private void migrateAll() {

    }
}
