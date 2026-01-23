package backend.mossy.boundedContext.market.in.product;

import backend.mossy.boundedContext.market.app.product.ProductSearchUseCase;
import backend.mossy.boundedContext.market.domain.product.ProductDocument;
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
@RequestMapping("/api/v1/product/search")
@RequiredArgsConstructor
@Slf4j
public class ApiV1ProductSearchController {
    private final ProductSearchUseCase productSearchUseCase;

    @GetMapping
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
