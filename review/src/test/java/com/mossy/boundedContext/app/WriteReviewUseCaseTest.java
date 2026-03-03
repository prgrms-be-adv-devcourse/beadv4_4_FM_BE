package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.boundedContext.out.ReviewableItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.review.enums.ReviewStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WriteReviewUseCaseTest {

    @Mock
    private ReviewableItemRepository reviewableItemRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private WriteReviewUseCase writeReviewUseCase;

    @Test
    @DisplayName("리뷰 작성 성공")
    void write_success() {
        Long userId = 1L;
        Long orderItemId = 10L;
        WriteReviewRequest request = new WriteReviewRequest("만족합니다", 5);
        ReviewableItem reviewableItem = ReviewableItem.builder()
                .orderItemId(orderItemId)
                .buyerId(userId)
                .productId(100L)
                .sellerId(200L)
                .reviewed(false)
                .build();

        when(reviewableItemRepository.findByOrderItemId(orderItemId)).thenReturn(Optional.of(reviewableItem));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = writeReviewUseCase.write(userId, orderItemId, request);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        Review savedReview = captor.getValue();

        assertThat(savedReview.getOrderItemId()).isEqualTo(orderItemId);
        assertThat(savedReview.getUserId()).isEqualTo(userId);
        assertThat(savedReview.getProductId()).isEqualTo(100L);
        assertThat(savedReview.getContent()).isEqualTo("만족합니다");
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getStatus()).isEqualTo(ReviewStatus.ACTIVE);

        assertThat(reviewableItem.isReviewed()).isTrue();
        assertThat(response.orderItemId()).isEqualTo(orderItemId);
        assertThat(response.productId()).isEqualTo(100L);
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 리뷰 가능 주문 항목 없음")
    void write_fail_reviewableItemNotFound() {
        when(reviewableItemRepository.findByOrderItemId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> writeReviewUseCase.write(1L, 10L, new WriteReviewRequest("test", 5)))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REVIEWABLE_ITEM_NOT_FOUND);

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 작성자 불일치")
    void write_fail_unauthorized() {
        ReviewableItem reviewableItem = ReviewableItem.builder()
                .orderItemId(10L)
                .buyerId(2L)
                .productId(100L)
                .sellerId(200L)
                .reviewed(false)
                .build();

        when(reviewableItemRepository.findByOrderItemId(10L)).thenReturn(Optional.of(reviewableItem));

        assertThatThrownBy(() -> writeReviewUseCase.write(1L, 10L, new WriteReviewRequest("test", 5)))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REVIEW_UNAUTHORIZED);

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 이미 작성됨")
    void write_fail_alreadyReviewed() {
        ReviewableItem reviewableItem = ReviewableItem.builder()
                .orderItemId(10L)
                .buyerId(1L)
                .productId(100L)
                .sellerId(200L)
                .reviewed(true)
                .build();

        when(reviewableItemRepository.findByOrderItemId(10L)).thenReturn(Optional.of(reviewableItem));

        assertThatThrownBy(() -> writeReviewUseCase.write(1L, 10L, new WriteReviewRequest("test", 5)))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_REVIEWED);

        verify(reviewRepository, never()).save(any(Review.class));
    }
}
