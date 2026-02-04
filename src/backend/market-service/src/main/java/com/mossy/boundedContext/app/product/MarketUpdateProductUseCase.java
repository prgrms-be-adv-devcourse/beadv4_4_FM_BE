package com.mossy.boundedContext.app.product;

import com.mossy.boundedContext.domain.product.Category;
import com.mossy.boundedContext.domain.product.Product;
import com.mossy.boundedContext.domain.product.event.ProductUpdatedEvent;
import com.mossy.boundedContext.infra.storage.adapter.S3Adapter;
import com.mossy.boundedContext.out.product.ProductRepository;
import com.mossy.boundedContext.out.product.CategoryRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.dto.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketUpdateProductUseCase {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;
    private final S3Adapter s3Adapter;

    @Value("${app.s3.dirs.product:product}") // 기본값 product 설정
    private String productDir;

    @Transactional
    public void update(Long productId, Long currentSellerId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));


        // 판매자 검증
        product.validateOwner(currentSellerId);

        // 카테고리 정보 조회
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 이미지 파일이 확인
        if (request.images() != null && !request.images().isEmpty()) {
            // 새 이미지 파일 S3 업로드
            List<String> newImageUrls = s3Adapter.uploadFiles(request.images(), productDir);

            // 엔티티 이미지 교체 및 기존 URL 리스트 획득
            List<String> oldImageUrls = product.updateImages(newImageUrls);

            // S3 기존 이미지 파일 삭제 (업로드가 성공한 후에 실행하는 것이 안전)
            s3Adapter.deleteFiles(oldImageUrls);
        }

        // 모든 정보를 포함하여 업데이트
        product.updateInfo(
                category, // 카테고리 추가
                request.name(),
                request.description(),
                request.price(),
                request.weight(),
                request.quantity(),
                request.status(),
                null
        );

        eventPublisher.publish(new ProductUpdatedEvent(product.getId()));
    }
}