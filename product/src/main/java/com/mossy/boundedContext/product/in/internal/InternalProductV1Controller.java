package com.mossy.boundedContext.product.in.internal;

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
@RequestMapping("/internal/v1/product")
@RequiredArgsConstructor
public class InternalProductV1Controller {

    @PostMapping("/stock/validate")
    public void validateStock(@RequestBody List<StockCheckRequest> items) {

    }
}
