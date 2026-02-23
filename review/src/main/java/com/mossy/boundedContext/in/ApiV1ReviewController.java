package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.ReviewFacade;
import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰")
@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ApiV1ReviewController {

    private final ReviewFacade reviewFacade;

    @Operation(summary = "리뷰 작성", description = "주문 아이템에 대한 리뷰를 작성합니다.")
    @PostMapping("/{orderItemId}")
    public ResponseEntity<RsData<ReviewResponse>> writeReview(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderItemId,
            @RequestBody @Valid WriteReviewRequest request
    ) {
        ReviewResponse response = reviewFacade.writeReview(userId, orderItemId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.success(SuccessCode.REVIEW_CREATED, response));
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 리뷰를 조회합니다.")
    @GetMapping("/{reviewId}")
    public RsData<ReviewResponse> getReview(
            @PathVariable Long reviewId
    ) {
        ReviewResponse response = reviewFacade.getReview(reviewId);
        return RsData.success(SuccessCode.REVIEW_GET, response);
    }

    @Operation(summary = "리뷰 목록 조회", description = "상품 ID로 리뷰 목록을 페이징 조회합니다.")
    @GetMapping
    public RsData<Page<ReviewResponse>> getReviewsByProductId(
            @RequestParam Long productId,
            @Parameter(hidden = true)
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ReviewResponse> response = reviewFacade.getReviewsByProductId(productId, pageable);
        return RsData.success(SuccessCode.REVIEW_LIST_GET, response);
    }
}
