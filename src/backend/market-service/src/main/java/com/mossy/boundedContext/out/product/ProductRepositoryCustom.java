package com.mossy.boundedContext.out.product;

import com.mossy.shared.market.dto.response.ProductInfoResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductInfoResponse> findCartItemsByBuyerId(Long buyerId);
}