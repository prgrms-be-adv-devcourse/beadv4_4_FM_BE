package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.app.ProductOptionAssembler;
import com.mossy.boundedContext.product.in.dto.request.ProductUpdateRequest;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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
        // 1. Fetch Join으로 한 번에 조회
        Product product = productRepository.findByIdWithAllDetails(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 2. 기본 정보 수정
        product.updateBaseInfo(request.basePrice());

        // 3. 어셈블러를 사용하여 아이템 버전 관리(단종 및 생성) 수행
        optionAssembler.configureForUpdate(product, request.productItems());

        // 4. (필요 시) 그룹 이름 수정 로직 추가
        // product.updateOptionGroupNames(request.optionGroups());
    }
}