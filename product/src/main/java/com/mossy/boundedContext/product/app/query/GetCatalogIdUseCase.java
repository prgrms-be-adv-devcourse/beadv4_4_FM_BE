package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCatalogIdUseCase {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Long execute(Long productItemId) {
        return productRepository.findCatalogProductIdByProductItemId(productItemId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_ITEM_NOT_FOUND));
    }
}
