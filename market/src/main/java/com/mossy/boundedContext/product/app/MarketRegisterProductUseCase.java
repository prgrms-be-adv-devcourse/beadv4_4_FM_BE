package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.product.app.mapper.ProductMapper;
import com.mossy.boundedContext.product.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
import com.mossy.boundedContext.product.domain.event.ProductRegisteredEvent;
import com.mossy.boundedContext.product.in.dto.request.ProductCreateRequest;
import com.mossy.boundedContext.product.out.ProductRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 2. 매퍼를 통한 엔티티 변환
        Product product = productMapper.toEntity(request, seller, catalog);

        // 3. 아이템 변환 및 추가
        request.items().forEach(itemRequest -> {
            String uniqueSku = productSupport.generateUniqueSkuCode(
                    seller.getId(), catalog.getId(), itemRequest.optionCombination());

            ProductItem item = productMapper.toItemEntity(itemRequest, uniqueSku);
            product.addProductItem(item); // 연관관계 편의 메서드 활용
        });

        productRepository.save(product);
        eventPublisher.publish(new ProductRegisteredEvent(product.getId()));

        return product;
    }
}
