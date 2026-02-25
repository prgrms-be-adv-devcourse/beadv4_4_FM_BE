package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.in.dto.response.ReviewableItemResponse;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final ReviewableItemRepository reviewableItemRepository;

    @Transactional(readOnly = true)
    public ReviewResponse get(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DomainException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ReviewableItemResponse> getPendingReviews(Long userId) {
        return reviewableItemRepository
                .findByBuyerIdAndReviewedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(ReviewableItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(ReviewResponse::from);
    }
}
