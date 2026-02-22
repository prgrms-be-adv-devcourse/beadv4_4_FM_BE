package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewFacade {

    private final CreateReviewableItemUseCase createReviewableItemUseCase;
    private final WriteReviewUseCase writeReviewUseCase;

    public void handleOrderPurchaseConfirmed(OrderPurchaseConfirmedEvent event) {
        createReviewableItemUseCase.create(event);
    }

    public ReviewResponse writeReview(Long userId, Long orderItemId, WriteReviewRequest request) {
        return writeReviewUseCase.write(userId, orderItemId, request);
    }
}
