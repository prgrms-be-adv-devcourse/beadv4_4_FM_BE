package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.boundedContext.out.external.ProductFeignClient;
import com.mossy.boundedContext.out.external.dto.ProductInfoResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.review.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WriteReviewUseCase {

    private final ReviewableItemRepository reviewableItemRepository;
    private final ReviewRepository reviewRepository;
    private final ProductFeignClient productFeignClient;

    @Transactional
    public ReviewResponse write(Long userId, Long orderItemId, WriteReviewRequest request) {
        ReviewableItem reviewableItem = reviewableItemRepository.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new DomainException(ErrorCode.REVIEWABLE_ITEM_NOT_FOUND));

        if (!reviewableItem.getBuyerId().equals(userId)) {
            throw new DomainException(ErrorCode.REVIEW_UNAUTHORIZED);
        }

        if (reviewableItem.isReviewed()) {
            throw new DomainException(ErrorCode.ALREADY_REVIEWED);
        }

        Review review = Review.builder()
                .orderItemId(orderItemId)
                .userId(reviewableItem.getBuyerId())
                .productId(reviewableItem.getProductId())
                .content(request.content())
                .rating(request.rating())
                .status(ReviewStatus.ACTIVE)
                .build();

        reviewRepository.save(review);
        reviewableItem.markAsReviewed();

        ProductInfoResponse productInfo = productFeignClient
                .getProductInfos(List.of(review.getProductId()))
                .stream().findFirst().orElse(null);

        return ReviewResponse.from(review, productInfo);
    }
}
