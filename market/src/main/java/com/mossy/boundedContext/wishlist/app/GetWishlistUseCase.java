package com.mossy.boundedContext.wishlist.app;

import com.mossy.boundedContext.wishlist.out.external.dto.WishlistProductResponse;
import com.mossy.boundedContext.wishlist.domain.Wishlist;
import com.mossy.boundedContext.wishlist.in.dto.response.WishlistResponse;
import com.mossy.boundedContext.wishlist.out.WishlistRepository;
import com.mossy.boundedContext.wishlist.out.external.WishlistFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWishlistUseCase {

    private final WishlistRepository wishlistRepository;
    private final WishlistFeignClient wishlistFeignClient;

    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByMarketUserIdOrderByCreatedAtDesc(userId);

        List<Long> productIds = wishlists.stream()
                .map(Wishlist::getProductId)
                .toList();

        Map<Long, WishlistProductResponse> productMap = wishlistFeignClient.findByIds(productIds)
                .stream()
                .collect(Collectors.toMap(WishlistProductResponse::productId, p -> p));

        return wishlists.stream()
            .map(w -> {
                WishlistProductResponse product = productMap.get(w.getProductId());
                return WishlistResponse.builder()
                        .wishlistId(w.getId())
                        .productId(w.getProductId())
                        .productName(product.productName())
                        .categoryName(product.categoryName())
                        .price(product.price())
                        .thumbnailUrl(product.thumbnailUrl())
                        .build();
            }).toList();
    }
}
