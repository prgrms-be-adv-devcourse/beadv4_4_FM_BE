package com.mossy.boundedContext.product.in;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.out.CatalogImageRepository;
import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.catalog.out.CatalogProductRepository;
import com.mossy.boundedContext.catalog.out.CatalogSearchRepository;
import com.mossy.boundedContext.catalog.out.CatalogSummaryDto;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductDataInit {
    private final ProductRepository productRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final CatalogSearchRepository catalogSearchRepository;
    private final CatalogImageRepository catalogImageRepository;

    @Bean
    public ApplicationRunner productDateInitRunner() {
        return args -> {
            catalogProductRepository.deleteAll();
            migrateAll();
        };
    }

    private void migrateAll() {
        List<CatalogProduct> catalogs = catalogProductRepository.findAll();

        // 최저가 및 판매자 수 집계 데이터 가져오기
        Map<Long, CatalogSummaryDto> summaryMap = productRepository.findAllCatalogSummaries()
                .stream()
                .collect(Collectors.toMap(CatalogSummaryDto::getCatalogId, s -> s));

        // 3. 모든 상품의 '썸네일' 이미지 한꺼번에 가져오기 (N+1 문제 방지)
        // isThumbnail이 true인 이미지들만 가져와서 targetId별로 맵핑
        Map<Long, String> thumbnailMap = catalogImageRepository.findByIsThumbnailTrue()
                .stream()
                .collect(Collectors.toMap(
                        CatalogImage::getTargetId,
                        CatalogImage::getImageUrl,
                        (existing, replacement) -> existing // 중복 시 첫 번째 것 유지
                ));

        // 3. Document로 변환
        List<CatalogDocument> documents = catalogs.stream()
                .map(catalog -> {
                    CatalogSummaryDto summary = summaryMap.get(catalog.getId());

                    // 집계 데이터가 없는 경우(판매 중인 상품이 없는 경우) 기본값 처리
                    Double minPrice = (summary != null) ? summary.getMinPrice().doubleValue() : 0.0;
                    Long sellerCount = (summary != null) ? summary.getSellerCount() : 0L;

                    String thumbnailUrl = thumbnailMap.getOrDefault(catalog.getId(), "default_image_url");

                    return CatalogDocument.from(catalog, thumbnailUrl, minPrice, sellerCount);
                })
                .toList();

        // 4. 엘라스틱서치에 저장
        if (!documents.isEmpty()) {
            catalogSearchRepository.saveAll(documents);
        }
    }
}
