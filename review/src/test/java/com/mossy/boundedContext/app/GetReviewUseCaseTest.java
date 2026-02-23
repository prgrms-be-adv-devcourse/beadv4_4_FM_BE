package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.boundedContext.out.ReviewRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.review.enums.ReviewStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReviewUseCaseTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GetReviewUseCase getReviewUseCase;

    @Test
    @DisplayName("리뷰 단건 조회 성공")
    void get_success() {
        Review review = Review.builder()
                .orderItemId(10L)
                .productId(20L)
                .userId(30L)
                .content("good")
                .rating(5)
                .status(ReviewStatus.ACTIVE)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewResponse response = getReviewUseCase.get(1L);

        assertThat(response.orderItemId()).isEqualTo(10L);
        assertThat(response.productId()).isEqualTo(20L);
        assertThat(response.userId()).isEqualTo(30L);
        assertThat(response.content()).isEqualTo("good");
        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.status()).isEqualTo(ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("리뷰 단건 조회 실패 - 없는 리뷰")
    void get_fail_notFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getReviewUseCase.get(999L))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회 성공")
    void getByProductId_success() {
        Review review = Review.builder()
                .orderItemId(11L)
                .productId(100L)
                .userId(30L)
                .content("nice")
                .rating(4)
                .status(ReviewStatus.ACTIVE)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(100L, pageable)).thenReturn(reviewPage);

        Page<ReviewResponse> result = getReviewUseCase.getByProductId(100L, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().productId()).isEqualTo(100L);
        assertThat(result.getContent().getFirst().content()).isEqualTo("nice");
    }
}
