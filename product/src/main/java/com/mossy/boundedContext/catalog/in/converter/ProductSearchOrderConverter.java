package com.mossy.boundedContext.catalog.in.converter;

import com.mossy.boundedContext.catalog.domain.enums.ProductSearchOrder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProductSearchOrderConverter implements Converter<String, ProductSearchOrder> {
    @Override
    public ProductSearchOrder convert(String source) {
        if (!StringUtils.hasText(source)) return ProductSearchOrder.POPULAR;
        return ProductSearchOrder.fromCode(source);
    }
}