package com.mossy.member.in.product;

import com.mossy.member.app.product.ProductFacade;
import com.mossy.member.domain.order.Order;
import com.mossy.member.domain.order.OrderDetail;
import com.mossy.member.domain.product.Product;
import com.mossy.member.domain.product.ProductDocument;
import com.mossy.member.domain.product.event.ProductDeletedEvent;
import com.mossy.member.domain.product.event.ProductRegisteredEvent;
import com.mossy.member.domain.product.event.ProductUpdatedEvent;
import com.mossy.member.out.order.OrderRepository;
import com.mossy.member.out.product.ProductElasticRepository;
import com.mossy.member.out.product.ProductRepository;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    private final ProductFacade  productFacade;
    private final ProductRepository productRepository;
    private final ProductElasticRepository psRepository;
    private final OrderRepository orderRepository;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentCompletedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        for (OrderDetail detail : order.getOrderDetails()) {
            productFacade.decreaseProductStock(detail.getProductId(), detail.getQuantity());
        }
    }

    // 상품 등록 후 엘라스틱서치에 등록
    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void productRegisteredEvent(ProductRegisteredEvent event) {
        log.info("thread name = {}", Thread.currentThread().getName());
        syncElasticsearch(event.productId());
    }

    // 상품 수정 후 엘라스틱서치에 반영
    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void productUpdatedEvent(ProductUpdatedEvent event) {
        log.info("thread name = {}", Thread.currentThread().getName());
        syncElasticsearch(event.productId());
    }

    // 상품 삭제 후 엘라스틱서치에서 하드 삭제
    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
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
