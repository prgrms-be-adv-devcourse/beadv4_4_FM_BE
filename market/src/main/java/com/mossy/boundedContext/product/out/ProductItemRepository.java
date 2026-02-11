package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    boolean existsBySkuCode(String skuCode);
}
