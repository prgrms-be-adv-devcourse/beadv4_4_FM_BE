package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.catalog.app.CatalogProductQueryService;
import com.mossy.boundedContext.catalog.app.dto.CatalogProductWithCategoryInfo;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.response.WishlistProductResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.boundedContext.product.out.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWishlistProductsUseCase {

    private final ProductRepository productRepository;
    private final CatalogProductQueryService catalogProductQueryService;

    @Transactional(readOnly = true)
    public List<WishlistProductResponse> execute(List<Long> productItemIds) {
        if (productItemIds == null || productItemIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ProductItem ID를 가지고 있는 Product 목록을 조회합니다.
        List<Product> products = productRepository.findDistinctByProductItems_IdIn(productItemIds);

        // 조회된 Product들에서 Catalog ID만 중복을 제거하여 추출
        List<Long> catalogIds = products.stream()
                .map(Product::getCatalogProductId)
                .distinct()
                .toList();

        // 카탈로그(+카테고리) 정보 가져오기
        List<CatalogProductWithCategoryInfo> catalogInfos =
                catalogProductQueryService.getProductInfos(catalogIds);

        Map<Long, CatalogProductWithCategoryInfo> catalogInfoMap = catalogInfos.stream()
                .collect(Collectors.toMap(CatalogProductWithCategoryInfo::id, info -> info));

        // Product 내부의 ProductItem을 객체 탐색으로 접근하여 최종 DTO 매핑
        return products.stream()
                .flatMap(product -> product.getProductItems().stream()
                        .filter(item -> productItemIds.contains(item.getId()))
                        .map(item -> {
                            CatalogProductWithCategoryInfo catalogInfo =
                                    catalogInfoMap.get(product.getCatalogProductId());

                            return new WishlistProductResponse(
                                    item.getId(),
                                    catalogInfo.name(),
                                    catalogInfo.categoryName(),
                                    item.getTotalPrice(),
                                    catalogInfo.thumbnailUrl()
                            );
                        })
                )
                .toList();
    }
}