package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.app.ProductOptionAssembler;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductOptionAssembler optionAssembler;

    public void updateProduct(Long productId, ProductUpdateRequest request) {

        Product product = productRepository.findByIdWithAllDetails(productId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        // 기본 정보 수정
        product.updateBaseInfo(request.basePrice());

        // 어셈블러를 사용하여 아이템 버전 관리(단종 및 생성) 수행
        optionAssembler.configureForUpdate(product, request.productItems());
    }
}