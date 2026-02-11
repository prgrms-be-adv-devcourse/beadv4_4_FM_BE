package com.mossy.boundedContext.product.app;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.out.MarketSellerRepository;
import com.mossy.boundedContext.product.domain.CatalogProduct;
import com.mossy.boundedContext.product.out.CatalogProductRepository;
import com.mossy.boundedContext.product.out.ProductItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductSupport {
    private final MarketSellerRepository marketSellerRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final ProductItemRepository productItemRepository;

    // 판매자 존재 여부 확인 및 반환
    public MarketSeller findSellerOrThrow(Long sellerId) {
        return marketSellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다. ID: " + sellerId));
    }

    // 카탈로그 상품 존재 여부 확인 및 반환
    public CatalogProduct findCatalogOrThrow(Long catalogId) {
        return catalogProductRepository.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("카탈로그 정보를 찾을 수 없습니다. ID: " + catalogId));
    }

    /**
     * 판매자별 고유 SKU 코드 생성
     * 규칙: S{판매자ID}-C{카탈로그ID}-{옵션명}
     */
    public String generateUniqueSkuCode(Long sellerId, Long catalogId, String optionCombination) {
        String optionSuffix = optionCombination.replaceAll("\\s+", "").toUpperCase();
        return String.format("S%d-C%d-%s", sellerId, catalogId, optionSuffix);
    }

    // SKU 코드 중복 체크
    public void validateSkuCode(String skuCode) {
        if (productItemRepository.existsBySkuCode(skuCode)) {
            throw new IllegalArgumentException("이미 사용 중인 SKU 코드입니다: " + skuCode);
        }
    }
}
