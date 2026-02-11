package com.mossy.boundedContext.product.app.mapper;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.product.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // 1. 기본 Product 정보 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "catalogProduct", source = "catalog")
    @Mapping(target = "productItems", ignore = true) // 아이템은 별도로 추가
    @Mapping(target = "status", constant = "FOR_SALE")
    Product toEntity(ProductCreateRequest request, MarketSeller seller, CatalogProduct catalog);

    // 2. 개별 ProductItem 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "skuCode", source = "skuCode")
    ProductItem toItemEntity(ProductItemRequest itemRequest, String skuCode);
}
