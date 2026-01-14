package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.shared.market.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
