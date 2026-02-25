package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.catalog.app.CatalogProductQueryService;
import com.mossy.boundedContext.catalog.app.dto.CatalogProductWithCategoryInfo;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductInfoResponse;
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
public class GetCartProductDetailsUseCase {

    private final ProductRepository productRepository;
    private final CatalogProductQueryService catalogProductQueryService;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductInfoResponse> execute(List<Long> productItemIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Product 엔티티 조회
        List<Product> products = productRepository.findAllById(productIds);

        // 조회된 Product들에서 Catalog ID만 중복을 제거하여 추출
        List<Long> catalogIds = products.stream()
                .map(Product::getCatalogProductId)
                .distinct()
                .toList();

        // 카탈로그(+카테고리) 정보 가져오기
        List<CatalogProductWithCategoryInfo> catalogInfos =
                catalogProductQueryService.getProductInfos(catalogIds);

        // 4. 카탈로그 정보를 ID 기준으로 쉽게 매핑할 수 있도록 Map으로 변환
        Map<Long, CatalogProductWithCategoryInfo> catalogInfoMap = catalogInfos.stream()
                // record를 사용하셨다면 .id() 호출, class라면 .getId() 호출
                .collect(Collectors.toMap(CatalogProductWithCategoryInfo::id, info -> info));

        // 5. Product와 Catalog 정보를 짝지어서 최종 DTO로 변환
        return products.stream()
                .map(product -> {
                    // Map에서 해당 상품의 카탈로그 정보를 꺼내옴
                    CatalogProductWithCategoryInfo catalogInfo =
                            catalogInfoMap.get(product.getCatalogProductId());

                    // Mapper를 통해 합체!
                    return productMapper.toProductInfoResponse(product, catalogInfo);
                })
                .toList();
    }
}