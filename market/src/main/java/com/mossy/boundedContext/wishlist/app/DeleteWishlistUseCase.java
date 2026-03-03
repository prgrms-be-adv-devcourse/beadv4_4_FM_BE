package com.mossy.boundedContext.wishlist.app;

import com.mossy.boundedContext.wishlist.out.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteWishlistUseCase {

    private final WishlistRepository wishlistRepository;

    @Transactional
    public void deleteWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByMarketUserIdAndProductId(userId, productId);
    }
}
