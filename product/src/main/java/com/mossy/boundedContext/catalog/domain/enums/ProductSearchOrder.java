package com.mossy.boundedContext.catalog.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProductSearchOrder {
    POPULAR("popular", "인기순"),
    LATEST("latest", "최신순"),
    PRICE_ASC("price_low", "낮은가격순"),
    PRICE_DESC("price_high", "높은가격순");

    private final String code;
    private final String description;

    public static ProductSearchOrder fromCode(String code) {
        return Arrays.stream(values())
                .filter(order -> order.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(POPULAR);
    }

    public boolean isPopular() {
        return this == POPULAR;
    }
}