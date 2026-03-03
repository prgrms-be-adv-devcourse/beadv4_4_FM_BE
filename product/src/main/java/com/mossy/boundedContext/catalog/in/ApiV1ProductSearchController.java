package com.mossy.boundedContext.catalog.in;

import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.app.CatalogSearchFacade;
import com.mossy.boundedContext.catalog.in.dto.command.ProductSearchCondition;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Product Search", description = "Elasticsearch 기반 상품 검색 API")
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ApiV1ProductSearchController {
    private final CatalogSearchFacade catalogSearchFacade;

    @GetMapping("/search")
    @Operation(
            summary = "상품 통합 검색 및 목록 조회",
            description = "키워드, 카테고리, 정렬 조건을 조합하여 상품 목록을 조회합니다."
    )
    public RsData<Page<CatalogDocument>> search(
            @ModelAttribute ProductSearchCondition condition,
            @ParameterObject Pageable pageable) {

        Page<CatalogDocument> responses = catalogSearchFacade.search(condition, pageable);
        return RsData.success(SuccessCode.GET_CATALOG_SUCCESS, responses);
    }
}
