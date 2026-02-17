package com.mossy.boundedContext.recommendation.app.usecase;

import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.boundedContext.recommendation.out.RecommendItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RecommendSyncItemUseCase {

    private final EmbeddingModel embeddingModel;
    private final RecommendItemRepository itemRepository;

    @Transactional
    public Mono<Void> syncItem(ProductCreateRequestDto request) {
        return itemRepository.existsByProductId(request.productId())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new DomainException(ErrorCode.ITEM_ALREADY_EXISTS));
                }

                return generateEmbedding(request.content())
                    .map(vectorStr -> RecommendItem.builder()
                        .productId(request.productId())
                        .content(request.content())
                        .vectorData(vectorStr)
                        .build())
                    .flatMap(item -> itemRepository.save(item).then());
            });
    }

    private Mono<String> generateEmbedding(String content) {
        return Mono.fromCallable(() -> embeddingModel.embed(content))
            .subscribeOn(Schedulers.boundedElastic())
            .map(this::convertVectorToString);
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