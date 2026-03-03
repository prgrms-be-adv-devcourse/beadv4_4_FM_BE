package com.mossy.boundedContext.recommendation.in.dto.request;

import lombok.Builder;

@Builder
public record ProductCreateRequestDto(
    Long productId,
    String content
) {

}
