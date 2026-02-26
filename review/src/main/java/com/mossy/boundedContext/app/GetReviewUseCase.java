package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.in.dto.response.ReviewableItemResponse;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.boundedContext.out.external.ProductFeignClient;
import com.mossy.boundedContext.out.external.dto.ProductInfoResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final ReviewableItemRepository reviewableItemRepository;
    private final ProductFeignClient productFeignClient;

    @Transactional(readOnly = true)
    public ReviewResponse get(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DomainException(ErrorCode.REVIEW_NOT_FOUND));

        ProductInfoResponse productInfo = productFeignClient
                .getProductInfos(List.of(review.getProductId()))
                .stream().findFirst().orElse(null);

        return ReviewResponse.from(review, productInfo);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getByProductId(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);

        Map<Long, ProductInfoResponse> productInfoMap = productFeignClient
                .getProductInfos(List.of(productId))
                .stream()
                .collect(Collectors.toMap(ProductInfoResponse::productId, p -> p));

        return reviews.map(review -> ReviewResponse.from(review, productInfoMap.get(review.getProductId())));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<Long> productIds = reviews.stream()
                .map(Review::getProductId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ProductInfoResponse> productInfoMap = productIds.isEmpty() ? Map.of() :
                productFeignClient.getProductInfos(productIds)
                        .stream()
                        .collect(Collectors.toMap(ProductInfoResponse::productId, p -> p));

        return reviews.map(review -> ReviewResponse.from(review, productInfoMap.get(review.getProductId())));
    }

    @Transactional(readOnly = true)
    public List<ReviewableItemResponse> getPendingReviews(Long userId) {
        List<ReviewableItem> items = reviewableItemRepository
                .findByBuyerIdAndReviewedFalseOrderByCreatedAtDesc(userId);

        List<Long> productIds = items.stream()
                .map(ReviewableItem::getProductId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ProductInfoResponse> productInfoMap = productIds.isEmpty() ? Map.of() :
                productFeignClient.getProductInfos(productIds)
                        .stream()
                        .collect(Collectors.toMap(ProductInfoResponse::productId, p -> p));

        return items.stream()
                .map(item -> ReviewableItemResponse.from(item, productInfoMap.get(item.getProductId())))
                .collect(Collectors.toList());
    }
}
