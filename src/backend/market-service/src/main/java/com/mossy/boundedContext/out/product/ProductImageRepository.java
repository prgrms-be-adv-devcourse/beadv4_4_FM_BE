package com.mossy.boundedContext.out.product;

import com.mossy.boundedContext.domain.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
