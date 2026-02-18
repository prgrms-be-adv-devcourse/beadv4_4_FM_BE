package com.mossy.boundedContext.catalog.in.dto.command;

import com.mossy.boundedContext.catalog.domain.enums.ProductSearchOrder;
import org.springframework.util.StringUtils;

public record ProductSearchCondition(
        String keyword,
        Long categoryId,
        ProductSearchOrder order
) {
    /**
     * 메인 화면 여부 판단
     * - 키워드 검색 없음
     * - 카테고리 선택 없음
     */
    public boolean isMainPage() {
        return !StringUtils.hasText(keyword) && categoryId == null;
    }

    /**
     * 실제 적용할 정렬 조건 결정
     * - 메인 화면: 인기순
     * - 검색/카테고리: 사용자 선택 또는 기본값(인기순)
     */
    public ProductSearchOrder getEffectiveOrder() {
        if (isMainPage()) {
            return ProductSearchOrder.POPULAR;
        }
        return order != null ? order : ProductSearchOrder.POPULAR;
    }
}