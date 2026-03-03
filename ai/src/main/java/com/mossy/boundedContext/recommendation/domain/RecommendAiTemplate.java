package com.mossy.boundedContext.recommendation.domain;

public class RecommendAiTemplate {

    public static final String CHAT_REASON_PROMPT = """
        사용자 질문: %s

        추천 상품 목록:
        %s

        위 상품들에 대해 사용자의 질문과 관련된 추천 사유를 각 상품별로 한 문장으로 작성해주세요.
        반드시 아래 JSON 형식으로만 응답하세요 (다른 텍스트 없이 JSON만):
        {"상품ID": "추천 사유", ...}
        """;
}
