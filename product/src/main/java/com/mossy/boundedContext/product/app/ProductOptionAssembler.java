package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogProductInfo;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.ProductOptionGroup;
import com.mossy.boundedContext.product.domain.ProductOptionValue;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.shared.market.enums.ProductItemStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductOptionAssembler {

    // 상품 등록
    public void configureForCreate(Product product,
                                   CatalogProductInfo catalogInfo, // 카탈로그 정보 추가
                                   List<ProductCreateRequest.OptionGroupRequest> groupDtos,
                                   List<ProductCreateRequest.ProductItemRequest> itemDtos) {

        Map<Long, ProductOptionGroup> groupMap = createGroups(product, groupDtos);

        for (var itemDto : itemDtos) {

            BigDecimal itemWeight = (itemDto.weight() != null)
                    ? itemDto.weight()
                    : catalogInfo.weight();

            String combination = generateCombinationString(itemDto.itemOptions(),
                    ProductCreateRequest.ItemOptionMappingRequest::value);

            // 공통 빌더 호출
            ProductItem newItem = createBaseItem(itemDto.additionalPrice(), itemDto.quantity(),
                    itemWeight, combination);

            for (var mappingDto : itemDto.itemOptions()) {
                addOptionValueToItem(newItem, mappingDto.masterId(), mappingDto.value(), groupMap);
            }
            product.addProductItem(newItem);
        }
    }

    // 상품 수정
    public void configureForUpdate(Product product,
                                   List<ProductUpdateRequest.ItemUpdateRequest> itemDtos) {

        Map<Long, ProductOptionGroup> groupMap = product.getOptionGroups().stream()
                .collect(Collectors.toMap(ProductOptionGroup::getMasterId, g -> g));

        for (var itemDto : itemDtos) {
            if (itemDto.id() != null) {
                product.discontinueItem(itemDto.id());
            }

            // 공통 메서드 활용: Update용 DTO 리스트 전달
            String combination = generateCombinationString(itemDto.itemOptions(),
                    ProductUpdateRequest.ValueUpdateRequest::value);

            ProductItem newItem = createBaseItem(itemDto.additionalPrice(), itemDto.quantity(),
                    itemDto.weight(), combination);

            for (var valCmd : itemDto.itemOptions()) {
                addOptionValueToItem(newItem, valCmd.masterId(), valCmd.value(), groupMap);
            }
            product.addProductItem(newItem);
        }
    }

    // --- 공통 메서드 ---

    private <T> String generateCombinationString(List<T> options, Function<T, String> valueExtractor) {
        if (options == null || options.isEmpty()) return "";

        return options.stream()
                .map(valueExtractor)
                .collect(Collectors.joining(" / "));
    }

    private ProductItem createBaseItem(BigDecimal price, Integer qty, BigDecimal weight, String combination) {
        return ProductItem.builder()
                .skuCode(generateSku())
                .additionalPrice(price)
                .quantity(qty)
                .weight(weight)
                .optionCombination(combination)
                .status(ProductItemStatus.ON_SALE)
                .build();
    }

    private Map<Long, ProductOptionGroup> createGroups(Product product,
                                                       List<ProductCreateRequest.OptionGroupRequest> groupDtos) {
        Map<Long, ProductOptionGroup> groupMap = new HashMap<>();

        for (var groupDto : groupDtos) {
            ProductOptionGroup group = ProductOptionGroup.builder()
                    .masterId(groupDto.masterId())
                    .name(groupDto.name())
                    .build();

            // 애그리거트 루트(Product)에 그룹 추가
            product.addOptionGroup(group);

            // 이후 아이템 생성 시 매핑하기 위해 맵에 보관 (key: masterId)
            groupMap.put(groupDto.masterId(), group);
        }

        return groupMap;
    }

    private void addOptionValueToItem(ProductItem newItem, Long masterId, String value, Map<Long, ProductOptionGroup> groupMap) {
        ProductOptionGroup group = groupMap.get(masterId);

        // value 객체 생성
        ProductOptionValue optionValue = ProductOptionValue.builder()
                .value(value)
                .optionGroup(group)
                .build();

        // ProductItme 연결
        newItem.addOptionValue(optionValue, group);
    }

    private String generateSku() {
        String timestamp = Long.toString(System.nanoTime(), 36).toUpperCase();
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "SKU-" + timestamp + "-" + randomPart;
    }
}
