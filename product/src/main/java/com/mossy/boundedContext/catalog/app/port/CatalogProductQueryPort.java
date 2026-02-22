package com.mossy.boundedContext.catalog.app.port;

import com.mossy.boundedContext.catalog.app.dto.CatalogSummaryData;

public interface CatalogProductQueryPort {
    CatalogSummaryData getCatalogSummary(Long catalogId);
}