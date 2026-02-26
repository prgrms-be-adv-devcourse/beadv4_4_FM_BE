package com.mossy.boundedContext.wishlist.out.external;

import com.mossy.boundedContext.wishlist.out.external.dto.WishlistProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "wishlist-product", url = "${mossy.feign.product-url}")
public interface WishlistFeignClient {

    @GetMapping("/internal/v1/products/wishlists")
    List<WishlistProductResponse> findByIds(@RequestParam("productItemIds") List<Long> productItemIds);
}
