package com.mossy.boundedContext.recommendation.app;

import com.mossy.boundedContext.recommendation.app.usecase.RecommendSyncItemUseCase;
import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RecommendFacade {

    private final RecommendSyncItemUseCase recommendSyncItemUseCase;

    public Mono<Void> syncItem(ProductCreateRequestDto request) {
        return recommendSyncItemUseCase.syncItem(request);
    }

}