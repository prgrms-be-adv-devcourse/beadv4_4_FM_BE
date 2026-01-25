package backend.mossy.boundedContext.market.in.product;

import backend.mossy.boundedContext.market.app.product.ProductSearchUseCase;
import backend.mossy.boundedContext.market.domain.product.ProductDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Product Search", description = "Elasticsearch 기반 상품 검색 API")
@RequestMapping("/api/v1/product/search")
@RequiredArgsConstructor
@Slf4j
public class ApiV1ProductSearchController {
    private final ProductSearchUseCase productSearchUseCase;

    @GetMapping
    @Operation(
            summary = "상품 검색",
            description = "상품명을 기준으로 상품 목록을 페이징 조회합니다."
    )
    public Page<ProductDocument> findByName(
            @RequestParam("name") String name,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size) {
        log.info("name={}", name);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductDocument> productsList = productSearchUseCase.findByName(name, pageable);

        return productsList;
    }
}
