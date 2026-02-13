package com.mossy.boundedContext.product.in.converter;

import com.mossy.boundedContext.product.domain.enums.ProductSearchOrder;
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