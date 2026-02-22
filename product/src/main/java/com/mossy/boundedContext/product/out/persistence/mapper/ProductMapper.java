package com.mossy.boundedContext.product.out.persistence.mapper;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.ProductOptionValue;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.shared.product.enums.ProductItemStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    // CatalogDto 매핑
    @Mapping(target = "categoryId", source = "catalog.category.id")
    @Mapping(target = "categoryName", source = "catalog.category.name")
    ProductDetailResponse.CatalogDto toCatalogDto(
            CatalogProduct catalog,
            List<CatalogImage> images);

    // ProductDto 매핑
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "optionGroups", expression = "java(toOptionGroupDtos(product))")
    @Mapping(target = "productItems", expression = "java(filterItems(product.getProductItems()))")
    ProductDetailResponse.ProductDto toProductDto(Product product);

    // 아이템 필터링 (Status 기준)
    default List<ProductDetailResponse.ProductItemDto> filterItems(List<ProductItem> items) {
        if (items == null) return null;
        return items.stream()
                .filter(item -> item.getStatus() == ProductItemStatus.ON_SALE ||
                        item.getStatus() == ProductItemStatus.PRE_ORDER ||
                        item.getStatus() == ProductItemStatus.OUT_OF_STOCK
                )
                .map(this::toItemDto)
                .toList();
    }

    // 옵션 그룹 매핑
    default List<ProductDetailResponse.OptionGroupDto> toOptionGroupDtos(Product product) {
        // 엔티티가 계산한 유효 옵션 값 집합을 한 번만 가져옴
        Set<String> validOptionValues = product.getValidOptionValues();

        return product.getOptionGroups().stream()
                .map(group -> {
                    List<String> filteredValues = group.getOptionValues().stream()
                            .map(ProductOptionValue::getValue)
                            .filter(validOptionValues::contains)
                            .distinct()
                            .toList();

                    if (filteredValues.isEmpty()) return null;
                    return new ProductDetailResponse.OptionGroupDto(group.getId(), group.getName(), filteredValues);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    // ProductItemDto 매핑
    @Mapping(target = "productItemsId", source = "id")
    ProductDetailResponse.ProductItemDto toItemDto(ProductItem item);
}