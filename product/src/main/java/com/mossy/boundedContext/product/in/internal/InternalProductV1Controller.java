package com.mossy.boundedContext.product.in.internal;

import com.mossy.boundedContext.product.app.ProductFacade;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import com.mossy.boundedContext.product.in.internal.dto.response.ProductInfoResponse;
import com.mossy.boundedContext.product.in.internal.dto.response.WishlistProductResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/wishlists")
    public List<WishlistProductResponse> getWishlists(
            @RequestParam("productItemIds") List<Long> productItemIds
    ) {
        return productFacade.getWishlistProductItems(productItemIds);
    }

    @GetMapping("/carts")
    public List<ProductInfoResponse> getCarts(
            @RequestParam("productItemIds") List<Long> productItemIds
    ) {
        return productFacade.getCartProductItems(productItemIds);
    }

}
