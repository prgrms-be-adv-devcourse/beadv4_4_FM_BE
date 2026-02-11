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


    @Mapping(target = "status", constant = "FOR_SALE")
    Product toEntity(ProductCreateRequest request, MarketSeller seller, CatalogProduct catalog);

    ProductItem toItemEntity(ProductItemRequest itemRequest, String skuCode);
}
