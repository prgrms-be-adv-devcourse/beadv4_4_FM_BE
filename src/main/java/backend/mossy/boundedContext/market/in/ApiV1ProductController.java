package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.ProductRequest;
import backend.mossy.shared.market.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final MarketFacade marketFacade;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductList() {
        return marketFacade
                .getProductList()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<Void> createProduct(@RequestBody @Valid ProductRequest request) {
        marketFacade.registerProduct(request);
        return new RsData<>("201", "상품이 등록되었습니다");
    }
}
