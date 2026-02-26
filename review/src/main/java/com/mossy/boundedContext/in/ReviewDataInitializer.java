package com.mossy.boundedContext.in;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.shared.review.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class ReviewDataInitializer implements CommandLineRunner {

    private final ReviewableItemRepository reviewableItemRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // ReviewableItem: 리뷰 미작성 (pending) 샘플 - buyerId=101, productId=1001, sellerId=201
        createReviewableItemIfAbsent(10001L, 101L, 1001L, 201L);
        createReviewableItemIfAbsent(10002L, 101L, 1002L, 201L);
        createReviewableItemIfAbsent(10003L, 101L, 1003L, 202L);

        // ReviewableItem: 리뷰 작성 완료 샘플 (is_reviewed=true)
        createReviewableItemIfAbsent(10004L, 101L, 1004L, 202L);
        createReviewableItemIfAbsent(10005L, 102L, 1001L, 201L);

        // Review: 작성된 리뷰 샘플
        createReviewIfAbsent(10004L, 1004L, 101L, "신선하고 좋아요! 다음에도 구매할게요.", 5);
        createReviewIfAbsent(10005L, 1001L, 102L, "배송이 빠르고 상품 상태도 좋았습니다.", 4);

        // 리뷰가 작성된 ReviewableItem은 reviewed 처리
        markAsReviewedIfNeeded(10004L);
        markAsReviewedIfNeeded(10005L);

        log.info("[ReviewDataInitializer] ReviewableItem 5건, Review 2건 초기 데이터 준비 완료");
    }

    private void createReviewableItemIfAbsent(Long orderItemId, Long buyerId, Long productId, Long sellerId) {
        if (reviewableItemRepository.existsByOrderItemId(orderItemId)) return;

        reviewableItemRepository.save(ReviewableItem.builder()
                .orderItemId(orderItemId)
                .buyerId(buyerId)
                .productId(productId)
                .sellerId(sellerId)
                .reviewed(false)
                .build());
    }

    private void createReviewIfAbsent(Long orderItemId, Long productId, Long userId, String content, int rating) {
        if (reviewRepository.existsByOrderItemId(orderItemId)) return;

        reviewRepository.save(Review.builder()
                .orderItemId(orderItemId)
                .productId(productId)
                .userId(userId)
                .content(content)
                .rating(rating)
                .status(ReviewStatus.ACTIVE)
                .build());
    }

    private void markAsReviewedIfNeeded(Long orderItemId) {
        reviewableItemRepository.findByOrderItemId(orderItemId).ifPresent(item -> {
            if (!item.isReviewed()) {
                item.markAsReviewed();
            }
        });
    }
}
