package com.mossy.boundedContext.recommendation.in;

import com.mossy.boundedContext.recommendation.app.RecommendFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recommendation", description = "AI 추천 API")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class ApiV1RecommendController {

    private final RecommendFacade recommendFacade;
}