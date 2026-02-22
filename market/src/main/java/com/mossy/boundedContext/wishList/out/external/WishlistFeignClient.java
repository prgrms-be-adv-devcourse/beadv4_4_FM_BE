package com.mossy.boundedContext.wishList.out.external;

import com.mossy.boundedContext.wishList.out.external.dto.WishlistProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "wishlist-product", url = "http://localhost:8090")
public interface WishlistFeignClient {

    @GetMapping("/internal/v1/wishlists/products")
    List<WishlistProductResponse> findByIds(@RequestParam("productItemIds") List<Long> productItemIds);
}
