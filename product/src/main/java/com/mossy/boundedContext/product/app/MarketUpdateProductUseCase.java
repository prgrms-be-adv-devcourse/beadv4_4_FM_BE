package com.mossy.boundedContext.product.app;

//@Service
//@RequiredArgsConstructor
//public class MarketUpdateProductUseCase {
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//    private final EventPublisher eventPublisher;
//    private final S3Adapter s3Adapter;
//
//    @Value("${app.s3.dirs.product:product}") // 기본값 product 설정
//    private String productDir;
//
//    @Transactional
//    public void update(Long productId, Long currentSellerId, ProductUpdateRequest request) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
//
//
//        // 판매자 검증
//        product.validateOwner(currentSellerId);
//
//        // 카테고리 정보 조회
//        Category category = categoryRepository.findById(request.categoryId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
//
//
//        // 모든 정보를 포함하여 업데이트
//        product.updateInfo(
//                category, // 카테고리 추가
//                request.name(),
//                request.description(),
//                request.price(),
//                request.weight(),
//                request.quantity(),
//                request.status(),
//                null
//        );
//
//        eventPublisher.publish(new ProductUpdatedEvent(product.getId()));
//    }
//}