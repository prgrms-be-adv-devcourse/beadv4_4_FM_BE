package com.mossy.boundedContext.in.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record WriteReviewRequest(
        @NotBlank String content,
        @Min(1) @Max(5) int rating
) {}
