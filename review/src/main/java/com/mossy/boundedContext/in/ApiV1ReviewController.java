package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.ReviewFacade;
import com.mossy.boundedContext.in.dto.request.WriteReviewRequest;
import com.mossy.boundedContext.in.dto.response.ReviewResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
