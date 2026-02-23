package com.mossy.boundedContext.product.in.internal;

import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Product", description = "FeignClient Controller")
@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class InternalProductV1Controller {
    private final ProductFacade productFacade;

    @PostMapping("/decrease")
    public void decreaseStock(@RequestBody List<StockCheckRequest> items) {
        productFacade.decreaseStock(items);
    }

    @PostMapping("/increase")
    public void increaseStock(@RequestBody List<StockCheckRequest> items) {
        productFacade.increaseStock(items);
    };
}
