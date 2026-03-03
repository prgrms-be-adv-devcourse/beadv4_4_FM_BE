package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateReviewableItemUseCase {

    private final ReviewableItemRepository reviewableItemRepository;

    @Transactional
    public void create(OrderPurchaseConfirmedEvent event) {
        event.orderItems().forEach(orderItem -> {
            // 멱등성 체크
            if (reviewableItemRepository.existsByOrderItemId(orderItem.orderItemId())) {
                log.warn("[멱등성] 이미 처리된 이벤트 skip. orderItemId={}", orderItem.orderItemId());
                return;
            }

            ReviewableItem reviewableItem = ReviewableItem.builder()
                    .orderItemId(orderItem.orderItemId())
                    .buyerId(event.buyerId())
                    .productId(orderItem.productItemId())
                    .sellerId(orderItem.sellerId())
                    .reviewed(false)
                    .build();

            reviewableItemRepository.save(reviewableItem);
        });
    }
}
