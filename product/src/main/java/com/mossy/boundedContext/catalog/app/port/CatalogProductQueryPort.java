package com.mossy.boundedContext.catalog.app.port;

import com.mossy.boundedContext.catalog.app.dto.CatalogReviewInfoDto;
import com.mossy.boundedContext.catalog.app.dto.CatalogSummaryData;

import java.util.List;
import java.util.Map;

public interface CatalogProductQueryPort {
    CatalogSummaryData getCatalogSummary(Long catalogId);

    Map<Long, CatalogReviewInfoDto> getReviewInfos(List<Long> catalogIds);
}