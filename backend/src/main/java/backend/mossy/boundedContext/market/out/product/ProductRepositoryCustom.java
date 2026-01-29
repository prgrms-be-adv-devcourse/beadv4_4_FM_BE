package backend.mossy.boundedContext.market.out.product;

import backend.mossy.shared.market.dto.response.ProductInfoResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductInfoResponse> findCartItemsByBuyerId(Long buyerId);
}