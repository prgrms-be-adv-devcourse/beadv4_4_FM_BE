package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetReviewUseCase {

    private final ReviewRepository reviewRepository;

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
}
