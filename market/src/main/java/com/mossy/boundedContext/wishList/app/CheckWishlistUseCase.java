package com.mossy.boundedContext.wishList.app;

import com.mossy.boundedContext.wishList.out.WishlistRepository;
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
