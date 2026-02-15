package com.mossy.boundedContext.product.app.query;

import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProductDetailUseCase {

    private final ProductRepository productRepository;

    public ProductDetailResponse execute(Long catalogProductId) {
        // Step 1: 리포지토리의 Custom 메서드 호출
        ProductDetailResponse response = productRepository.findProductDetail(catalogProductId);

        // Step 2: 예외 처리 (상품이 없는 경우 등)
        if (response == null) {
            throw new DomainException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return response;
    }
}
