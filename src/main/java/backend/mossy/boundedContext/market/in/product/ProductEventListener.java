package backend.mossy.boundedContext.market.in.product;

import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.ProductDocument;
import backend.mossy.boundedContext.market.domain.product.event.ProductDeletedEvent;
import backend.mossy.boundedContext.market.domain.product.event.ProductRegisteredEvent;
import backend.mossy.boundedContext.market.domain.product.event.ProductUpdatedEvent;
import backend.mossy.boundedContext.market.out.product.ProductElasticRepository;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    private final ProductRepository productRepository;
    private final ProductElasticRepository psRepository;

    //    @TransactionalEventListener(phase = AFTER_COMMIT)
    //    @Transactional(propagation = REQUIRES_NEW)
    //    public void handle(결제 이벤트) { marketFacade.decreaseProductStock(productId, 1);}

    // 상품 등록 후 엘라스틱서치에 등록
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void productRegisteredEvent(ProductRegisteredEvent event) {
        log.info("thread name = {}", Thread.currentThread().getName());
        syncElasticsearch(event.productId());
    }

    // 상품 수정 후 엘라스틱서치에 반영
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void productUpdatedEvent(ProductUpdatedEvent event) {
        log.info("thread name = {}", Thread.currentThread().getName());
        syncElasticsearch(event.productId());
    }

    // 상품 삭제 후 엘라스틱서치에서 하드 삭제
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void productDeletedEvent(ProductDeletedEvent event) {
        try {
            psRepository.deleteById(event.productId());

            log.info("Elasticsearch 삭제 완료 - 상품 ID: {}", event.productId());
        } catch (Exception e) {
            log.error("Elasticsearch 삭제 실패 - 상품 ID: {}", event.productId(), e);
        }
    }

    // Product entity -> document으로 변환 후 저장
    private void syncElasticsearch(Long productId) {
        try {
            Product product = productRepository.findByIdWithDetails(productId)
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

            ProductDocument document = ProductDocument.from(product);
            psRepository.save(document);

            log.info("Elasticsearch 동기화 완료 - 상품 ID: {}", productId);
        } catch (Exception e) {
            log.error("Elasticsearch 동기화 실패 - 상품 ID: {}", productId, e);
        }
    }
}
