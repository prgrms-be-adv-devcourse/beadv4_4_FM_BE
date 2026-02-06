package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
