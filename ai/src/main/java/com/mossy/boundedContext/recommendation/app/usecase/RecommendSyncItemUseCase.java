package com.mossy.boundedContext.recommendation.app.usecase;

import com.mossy.boundedContext.recommendation.app.mapper.RecommendMapper;
import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.boundedContext.recommendation.out.repository.RecommendItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.market.event.ProductUpdatedEvent;
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
    private final RecommendMapper recommendMapper;

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

    @Transactional
    public Mono<Void> syncUpdate(ProductUpdatedEvent event) {
        return itemRepository.findByProductId(event.productId())
            .switchIfEmpty(Mono.error(new DomainException(ErrorCode.ITEM_NOT_FOUND)))
            .flatMap(existingItem -> {
                String newContent = recommendMapper.toContent(event);
                boolean contentChanged = !existingItem.getContent().equals(newContent);

                if (!contentChanged) {
                    RecommendItem updated = existingItem.toBuilder()
                        .price(event.price())
                        .status(event.status())
                        .build();
                    return itemRepository.save(updated).then();
                }

                return generateEmbedding(newContent)
                    .map(vectorStr -> existingItem.toBuilder()
                        .content(newContent)
                        .vectorData(vectorStr)
                        .price(event.price())
                        .status(event.status())
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