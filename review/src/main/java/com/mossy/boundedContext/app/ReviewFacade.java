package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewFacade {

    private final CreateReviewableItemUseCase createReviewableItemUseCase;
    private final WriteReviewUseCase writeReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;

    public void handleOrderPurchaseConfirmed(OrderPurchaseConfirmedEvent event) {
        createReviewableItemUseCase.create(event);
    }

    public ReviewResponse writeReview(Long userId, Long orderItemId, WriteReviewRequest request) {
        return writeReviewUseCase.write(userId, orderItemId, request);
    }

    public ReviewResponse getReview(Long reviewId) {
        return getReviewUseCase.get(reviewId);
    }

    public Page<ReviewResponse> getReviewsByProductId(Long productId, Pageable pageable) {
        return getReviewUseCase.getByProductId(productId, pageable);
    }
}
