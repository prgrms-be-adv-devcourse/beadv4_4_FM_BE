package com.mossy.boundedContext.recommendation.app;

import com.mossy.boundedContext.recommendation.app.usecase.RecommendGenerateReasonUseCase;
import com.mossy.boundedContext.recommendation.app.usecase.RecommendSearchItemsUseCase;
import com.mossy.boundedContext.recommendation.app.usecase.RecommendSyncItemUseCase;
import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.boundedContext.recommendation.in.dto.response.RecommendProductResponse;
import com.mossy.boundedContext.recommendation.out.ProductServiceAdapter;
import com.mossy.boundedContext.recommendation.out.external.dto.response.ProductResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.product.event.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendFacade {

    private final RecommendSyncItemUseCase recommendSyncItemUseCase;
    private final RecommendSearchItemsUseCase recommendSearchItemsUseCase;
    private final RecommendGenerateReasonUseCase recommendGenerateReasonUseCase;
    private final ProductServiceAdapter marketServiceAdapter;
    private final EmbeddingModel embeddingModel;

    public Mono<Void> syncItem(ProductCreateRequestDto request) {
        return recommendSyncItemUseCase.syncItem(request);
    }

    public Mono<Void> syncUpdate(ProductUpdatedEvent event) {
        return recommendSyncItemUseCase.syncUpdate(event);
    }

    public Mono<List<ProductResponse>> searchRecommendations(Long productId) {
        return recommendSearchItemsUseCase.searchSimilarProductIds(productId)
            .flatMap(marketServiceAdapter::getProductsFilteredByReviews);
    }

    public Mono<List<RecommendProductResponse>> chatRecommend(String query) {
        return embedQuery(query)
            .flatMap(recommendSearchItemsUseCase::searchByVector)
            .flatMap(marketServiceAdapter::getProductDetails)
            .flatMap(products ->
                recommendGenerateReasonUseCase.generateReasons(query, products)
                    .map(reasons -> assembleResponse(products, reasons))
            );
    }

    private Mono<String> embedQuery(String query) {
        return Mono.fromCallable(() -> embeddingModel.embed(query))
            .subscribeOn(Schedulers.boundedElastic())
            .map(this::convertVectorToString)
            .onErrorMap(e -> new DomainException(ErrorCode.EMBEDDING_FAILED));
    }

    private List<RecommendProductResponse> assembleResponse(
        List<ProductResponse> products,
        Map<Long, String> reasons
    ) {
        return products.stream()
            .map(p -> new RecommendProductResponse(
                p.productId(),
                p.name(),
                p.price(),
                p.thumbnailUrl(),
                reasons.getOrDefault(p.productId(), "")
            ))
            .toList();
    }

    private String convertVectorToString(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            if (i < vector.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
