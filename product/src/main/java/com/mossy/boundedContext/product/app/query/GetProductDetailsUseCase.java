package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.catalog.app.port.CatalogProductQueryPort;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProductDetailsUseCase {

    private final ProductRepository productRepository;
    private final CatalogProductQueryPort catalogProductQueryPort;

    @Transactional(readOnly = true)
    public List<ProductResponse> execute(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        List<Long> catalogIds = products.stream()
                .map(Product::getCatalogProductId)
                .distinct()
                .toList();

        Map<Long, CatalogReviewInfoDto> catalogMap = catalogProductQueryPort.getReviewInfos(catalogIds);

        return products.stream()
                .map(product -> {
                    CatalogReviewInfoDto info = catalogMap.get(product.getCatalogProductId());

                    return new ProductResponse(
                            product.getId(),
                            info.name(),
                            info.categoryName(), // 카테고리 이름 추가
                            product.getMinTotalPrice(),
                            info.thumbnail(),
                            info.reviewCount().intValue(),
                            info.averageRating()
                    );
                })
                .collect(Collectors.toList());
    }
}