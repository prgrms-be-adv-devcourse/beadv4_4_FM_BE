package com.mossy.boundedContext.recommendation.app.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mossy.boundedContext.recommendation.domain.RecommendAiTemplate;
import com.mossy.boundedContext.recommendation.out.external.dto.response.ProductResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendGenerateReasonUseCase {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public Mono<Map<Long, String>> generateReasons(String query, List<ProductResponse> products) {
        return Mono.fromCallable(() -> {
                String prompt = buildPrompt(query, products);
                String response = chatModel.call(prompt);
                return parseReasons(response);
            })
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(e -> !(e instanceof DomainException),
                e -> new DomainException(ErrorCode.AI_GENERATION_FAILED));
    }

    private String buildPrompt(String query, List<ProductResponse> products) {
        String productList = products.stream()
            .map(p -> "[상품 ID: %d] 상품명: %s, 카테고리: %s, 가격: %d원"
                .formatted(p.productId(), p.name(), p.categoryName(), p.price()))
            .collect(Collectors.joining("\n"));

        return RecommendAiTemplate.CHAT_REASON_PROMPT.formatted(query, productList);
    }

    private Map<Long, String> parseReasons(String response) {
        try {
            String json = response.strip();
            // LLM 응답에서 JSON 블록만 추출
            if (json.contains("{")) {
                json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
            }

            Map<String, String> raw = objectMapper.readValue(json, new TypeReference<>() {});
            return raw.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> Long.parseLong(e.getKey()),
                    Map.Entry::getValue
                ));
        } catch (Exception e) {
            throw new DomainException(ErrorCode.AI_GENERATION_FAILED);
        }
    }
}
