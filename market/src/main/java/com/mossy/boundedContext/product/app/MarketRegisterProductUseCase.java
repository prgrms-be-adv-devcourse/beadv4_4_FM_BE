package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.product.app.mapper.ProductMapper;
import com.mossy.boundedContext.product.domain.*;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.out.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketRegisterProductUseCase {
    private final ProductRepository productRepository;
    private final ProductSupport productSupport;
    private final ProductMapper productMapper;
    private final EventPublisher eventPublisher;

    @Transactional
    public Product register(ProductCreateRequest request) {
        // 1. 검증 및 조회
        MarketSeller seller = productSupport.findSellerOrThrow(request.sellerId());
        CatalogProduct catalog = productSupport.findCatalogOrThrow(request.catalogId());

        // 2. 엔티티 변환
        Product product = productMapper.toEntity(request, seller, catalog);

        // 옵션 그룹(OptionGroup) 생성
        List<ProductOptionGroup> optionGroups = new ArrayList<>();
        if (request.optionGroupNames() != null) {
            for (String groupName : request.optionGroupNames()) {
                ProductOptionGroup group = ProductOptionGroup.builder()
                        .name(groupName)
                        .build();
                product.addOptionGroup(group);
                optionGroups.add(group);
            }
        }

        // 옵션값(OptionValue) 저장
        request.items().forEach(itemRequest -> {
            String uniqueSku = productSupport.generateUniqueSkuCode(
                    seller.getId(), catalog.getId(), itemRequest.optionCombination());

            ProductItem item = productMapper.toItemEntity(itemRequest, uniqueSku);
            product.addProductItem(item);

            // 상세 옵션값 파싱 및 생성
            String[] values = itemRequest.optionCombination().split("/");
            for (int i = 0; i < values.length; i++) {
                ProductOptionValue optionValue = ProductOptionValue.builder()
                        .optionGroup(optionGroups.get(i))
                        .value(values[i].trim())
                        .build();
                item.addOptionValue(optionValue);
            }
        });

        productRepository.save(product);
        eventPublisher.publish(new ProductRegisteredEvent(product.getId()));

        return product;
    }
}
