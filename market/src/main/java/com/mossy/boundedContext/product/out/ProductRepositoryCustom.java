package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductInfoResponse> findCartItemsByBuyerId(Long buyerId);
}