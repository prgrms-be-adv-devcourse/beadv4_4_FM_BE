package com.mossy.boundedContext.product.app;

//@Service
//@RequiredArgsConstructor
//public class MarketDeleteProductUseCase {
//    private final ProductRepository productRepository;
//    private final EventPublisher eventPublisher;
//
//    @Transactional
//    public void delete(Long productId, Long currentSellerId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
//
//        product.validateOwner(currentSellerId);
//        product.delete();
//
//        eventPublisher.publish(new ProductDeletedEvent(productId));
//    }
//}
