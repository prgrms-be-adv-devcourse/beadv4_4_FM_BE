package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.product.Category;
import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.event.ProductUpdatedEvent;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.boundedContext.market.out.product.CategoryRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketUpdateProductUseCase {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;
    private final S3Service s3Service;

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
            List<String> newImageUrls = s3Service.uploadFiles(request.images());

            // 엔티티 이미지 교체 및 기존 URL 리스트 획득
            List<String> oldImageUrls = product.updateImages(newImageUrls);

            // S3 기존 이미지 파일 삭제 (업로드가 성공한 후에 실행하는 것이 안전)
            s3Service.deleteFiles(oldImageUrls);
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