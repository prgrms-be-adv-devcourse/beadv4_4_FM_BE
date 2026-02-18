package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;

public interface ProductRepositoryCustom {
    ProductDetailResponse findProductDetail(Long productId);
}
