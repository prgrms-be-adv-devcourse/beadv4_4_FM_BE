package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.ProductOptionGroup;
import com.mossy.boundedContext.product.domain.ProductOptionValue;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductOptionConfigureUseCase {

    public void configure(Product product,
                          List<ProductCreateRequest.OptionGroupRequest> options,
                          List<ProductCreateRequest.ProductItemRequest> items) {

// 1. 옵션 구성
        options.forEach(groupDto -> {
            ProductOptionGroup group = ProductOptionGroup.builder()
                    .masterId(groupDto.masterId())
                    .name(groupDto.name())
                    .build();
            product.addOptionGroup(group);

        });

// 2. 단품(Item) 및 선택 값(Value) 구성
        items.forEach(itemDto -> {
            String generatedSku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            ProductItem item = ProductItem.builder()
                    .skuCode(generatedSku)
                    .optionCombination(itemDto.optionCombination())
                    .additionalPrice(itemDto.additionalPrice())
                    .quantity(itemDto.quantity())
                    .build();

// 3. 각 단품이 어떤 옵션을 가졌는지 매핑
            itemDto.itemOptions().forEach(optionDto -> {
                ProductOptionValue optionValue = ProductOptionValue.builder()
                        .value(optionDto.value())
                        .optionGroupId(optionDto.masterId())
                        .build();

                item.addOptionValue(optionValue);
            });

            product.addProductItem(item);
        });
    }
}
