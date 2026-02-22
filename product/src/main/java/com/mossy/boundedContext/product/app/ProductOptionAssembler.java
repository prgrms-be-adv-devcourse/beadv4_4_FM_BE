package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.catalog.app.dto.CatalogProductInfo;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.ProductOptionGroup;
import com.mossy.boundedContext.product.domain.ProductOptionValue;
import com.mossy.boundedContext.product.in.rest.dto.request.OptionValueRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.in.rest.dto.request.ProductUpdateRequest;
import com.mossy.shared.product.enums.ProductItemStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductOptionAssembler {

    // 상품 등록
    public void configureForCreate(Product product,
                                   CatalogProductInfo catalogInfo,
                                   List<ProductCreateRequest.OptionGroupRequest> groupDtos,
                                   List<ProductCreateRequest.ProductItemRequest> itemDtos) {

        Map<Long, ProductOptionGroup> groupMap = createGroups(product, groupDtos);

        for (var itemDto : itemDtos) {
            // 아이템별 무게가 없으면 카탈로그 기본값 사용
            BigDecimal finalWeight = Optional.ofNullable(itemDto.weight())
                    .orElse(catalogInfo.weight());

            addOptionItemToProduct(
                    product,
                    generateSku(),
                    itemDto.additionalPrice(),
                    itemDto.quantity(),
                    finalWeight,
                    itemDto.itemOptions(),
                    groupMap
            );
        }
    }

    // 상품 수정
    public void configureForUpdate(Product product,
                                   List<ProductUpdateRequest.ItemUpdateRequest> itemDtos) {

        Map<Long, ProductOptionGroup> groupMap = product.getOptionGroups().stream()
                .collect(Collectors.toMap(ProductOptionGroup::getMasterId, g -> g));

        for (var itemDto : itemDtos) {
            if (itemDto.id() != null) {
                ProductItem oldItem = product.findItem(itemDto.id());

                if (isDataChanged(oldItem, itemDto)) {
                    oldItem.markAsSuspended();

                    // 수정 시 기존 SKU를 그대로 계승
                    addOptionItemToProduct(
                            product,
                            oldItem.getSkuCode(),
                            itemDto.additionalPrice(),
                            itemDto.quantity(),
                            itemDto.weight(),
                            itemDto.itemOptions(),
                            groupMap
                    );
                }
            } else {
                // 새로 추가된 옵션 아이템은 신규 SKU 발급
                addOptionItemToProduct(
                        product,
                        generateSku(),
                        itemDto.additionalPrice(),
                        itemDto.quantity(),
                        itemDto.weight(),
                        itemDto.itemOptions(),
                        groupMap
                );
            }
        }
    }

    // --- 공통 메서드 ---
    private <T extends OptionValueRequest> void addOptionItemToProduct(Product product,
                                                                       String sku,
                                                                       BigDecimal additionalPrice,
                                                                       Integer quantity,
                                                                       BigDecimal weight,
                                                                       List<T> itemOptions,
                                                                       Map<Long, ProductOptionGroup> groupMap) {

        String combination = generateCombinationString(itemOptions, OptionValueRequest::value);

        // 가격 계산 Null 방어
        BigDecimal basePrice = Optional.ofNullable(product.getBasePrice()).orElse(BigDecimal.ZERO);
        BigDecimal safeAdditionalPrice = Optional.ofNullable(additionalPrice).orElse(BigDecimal.ZERO);

        ProductItem newItem = ProductItem.builder()
                .skuCode(sku)
                .additionalPrice(safeAdditionalPrice)
                .totalPrice(basePrice.add(safeAdditionalPrice))
                .quantity(quantity)
                .weight(weight)
                .optionCombination(combination)
                .status(ProductItemStatus.ON_SALE)
                .build();

        for (var opt : itemOptions) {
            addOptionValueToItem(newItem, opt.masterId(), opt.value(), groupMap);
        }

        product.addProductItem(newItem);
    }

    private boolean isDataChanged(ProductItem oldItem, ProductUpdateRequest.ItemUpdateRequest dto) {
        // BigDecimal 비교
        if (oldItem.getAdditionalPrice().compareTo(dto.additionalPrice()) != 0) return true;
        if (!oldItem.getQuantity().equals(dto.quantity())) return true;

        // Weight가 null일 수 있는 경우에 대한 방어 로직 포함 비교
        BigDecimal newWeight = Optional.ofNullable(dto.weight()).orElse(BigDecimal.ZERO);
        if (oldItem.getWeight().compareTo(newWeight) != 0) return true;

        // 옵션 조합 비교
        String newCombination = generateCombinationString(dto.itemOptions(), OptionValueRequest::value);
        return !oldItem.getOptionCombination().equals(newCombination);
    }

    private <T> String generateCombinationString(List<T> options, Function<T, String> valueExtractor) {
        if (options == null || options.isEmpty()) return "";
        return options.stream()
                .map(valueExtractor)
                .collect(Collectors.joining(" / "));
    }

    private Map<Long, ProductOptionGroup> createGroups(Product product,
                                                       List<ProductCreateRequest.OptionGroupRequest> groupDtos) {
        Map<Long, ProductOptionGroup> groupMap = new HashMap<>();
        for (var groupDto : groupDtos) {
            ProductOptionGroup group = ProductOptionGroup.builder()
                    .masterId(groupDto.masterId())
                    .name(groupDto.name())
                    .build();
            product.addOptionGroup(group);
            groupMap.put(groupDto.masterId(), group);
        }
        return groupMap;
    }

    private void addOptionValueToItem(ProductItem newItem, Long masterId, String value, Map<Long, ProductOptionGroup> groupMap) {
        ProductOptionGroup group = groupMap.get(masterId);
        ProductOptionValue optionValue = ProductOptionValue.builder()
                .value(value)
                .optionGroup(group)
                .build();
        newItem.addOptionValue(optionValue, group);
    }

    private String generateSku() {
        return "SKU-" + Long.toString(System.nanoTime(), 36).toUpperCase() +
                "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
