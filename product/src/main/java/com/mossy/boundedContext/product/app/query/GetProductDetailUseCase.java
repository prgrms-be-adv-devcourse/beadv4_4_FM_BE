package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProductDetailUseCase {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductDetailResponse execute(Long catalogProductId) {

        ProductDetailResponse response = productRepository.findProductDetail(catalogProductId);

        if (response == null) {
            throw new DomainException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return response;
    }
}
