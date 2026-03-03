package com.mossy.boundedContext.wishlist.app;

import com.mossy.boundedContext.wishlist.out.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckWishlistUseCase {

    private final WishlistRepository wishlistRepository;

    @Transactional(readOnly = true)
    public boolean checkWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByMarketUserIdAndProductId(userId, productId);
    }
}
