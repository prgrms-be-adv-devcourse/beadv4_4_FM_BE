package com.mossy.boundedContext.product.out.persistence.mapper;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.ProductOptionGroup;
import com.mossy.boundedContext.product.domain.ProductOptionValue;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    // CatalogDto 매핑
    @Mapping(target = "categoryName", source = "categoryName")
    ProductDetailResponse.CatalogDto toCatalogDto(
            CatalogProduct catalog,
            String categoryName,
            List<CatalogImage> images);

    // ProductDto 매핑
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "optionGroups", expression = "java(mapToOptionGroupDtos(product.getOptionGroups(), product.getProductItems()))")
    @Mapping(target = "productItems", source = "productItems")
    ProductDetailResponse.ProductDto toProductDto(Product product);

    // ProductItemDto 매핑
    @Mapping(target = "productItemsId", source = "id")
    ProductDetailResponse.ProductItemDto toItemDto(ProductItem item);

    // 옵션 그룹 중복 제거 로직
    default List<ProductDetailResponse.OptionGroupDto> mapToOptionGroupDtos(
            List<ProductOptionGroup> groups, List<ProductItem> items) {

        return groups.stream().map(group -> {
            List<String> values = items.stream()
                    .flatMap(item -> item.getOptionValues().stream())
                    .filter(val -> val.getOptionGroupId().equals(group.getId()))
                    .map(ProductOptionValue::getValue)
                    .distinct()
                    .toList();

            return new ProductDetailResponse.OptionGroupDto(group.getId(), group.getName(), values);
        }).toList();
    }
}