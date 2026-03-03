package com.mossy.boundedContext.recommendation.app.usecase;

import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import com.mossy.boundedContext.recommendation.domain.RecommendPolicy;
import com.mossy.boundedContext.recommendation.domain.UserProductClick;
import com.mossy.boundedContext.recommendation.out.repository.RecommendItemRepository;
import com.mossy.boundedContext.recommendation.out.repository.UserProductClickRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendPersonalizedUseCase {

    private final UserProductClickRepository clickRepository;
    private final RecommendItemRepository itemRepository;

    /**
     * 유저의 클릭 이력 기반 개인화 추천 상품 ID 목록 반환
     * 1) 유저의 클릭 이력 조회 (클릭수 내림차순, 상위 N개)
     * 2) 클릭한 각 상품의 벡터 데이터 조회
     * 3) 클릭수 가중 평균 벡터 계산
     * 4) 가중 평균 벡터로 유사 상품 검색
     */
    public Mono<List<Long>> execute(Long userId) {
        return clickRepository.findByUserIdOrderByClickCountDesc(userId)
            .take(RecommendPolicy.PERSONAL_CLICK_HISTORY_LIMIT)
            .collectList()
            .flatMap(clicks -> {
                if (clicks.isEmpty()) {
                    return Mono.just(List.<Long>of());
                }
                return buildWeightedVector(clicks)
                    .flatMap(vectorStr -> itemRepository
                        .findTopSimilarProductIdsByVector(vectorStr, RecommendPolicy.PERSONAL_RECOMMEND_TOP_N)
                        .collectList()
                    );
            });
    }

    //클릭 이력의 가중 평균 벡터 계산
    private Mono<String> buildWeightedVector(List<UserProductClick> clicks) {
        List<Long> productIds = clicks.stream()
            .map(UserProductClick::getProductId)
            .toList();

        return Flux.fromIterable(productIds)
            .flatMap(itemRepository::findByProductId)
            .collectList()
            .map(items -> {
                // productId -> RecommendItem 매핑
                float[] weightedSum = null;
                long totalWeight = 0;

                for (UserProductClick click : clicks) {
                    RecommendItem item = items.stream()
                        .filter(i -> i.getProductId().equals(click.getProductId()))
                        .findFirst()
                        .orElse(null);

                    if (item == null || item.getVectorData() == null) {
                        continue;
                    }

                    float[] vector = parseVector(item.getVectorData());
                    if (vector == null) {
                        continue;
                    }

                    if (weightedSum == null) {
                        weightedSum = new float[vector.length];
                    }

                    long weight = click.getClickCount();
                    totalWeight += weight;

                    for (int i = 0; i < vector.length; i++) {
                        weightedSum[i] += vector[i] * weight;
                    }
                }

                if (weightedSum == null || totalWeight == 0) {
                    return "";
                }

                // 가중 평균
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < weightedSum.length; i++) {
                    sb.append(weightedSum[i] / totalWeight);
                    if (i < weightedSum.length - 1) sb.append(",");
                }
                sb.append("]");
                return sb.toString();
            });
    }

    // "[0.1,0.2,0.3]" 형태의 문자열을 float[] 로 파싱
    private float[] parseVector(String vectorStr) {
        try {
            String cleaned = vectorStr.trim();
            if (cleaned.startsWith("[")) cleaned = cleaned.substring(1);
            if (cleaned.endsWith("]")) cleaned = cleaned.substring(0, cleaned.length() - 1);

            String[] parts = cleaned.split(",");
            float[] vector = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Float.parseFloat(parts[i].trim());
            }
            return vector;
        } catch (Exception e) {
            log.warn("벡터 파싱 실패: {}", vectorStr, e);
            return null;
        }
    }
}

