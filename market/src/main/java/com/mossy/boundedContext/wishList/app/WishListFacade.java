package com.mossy.boundedContext.wishList.app;

import com.mossy.boundedContext.wishList.in.dto.response.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListFacade {

    private final AddWishlistUseCase addWishlistUseCase;
    private final DeleteWishlistUseCase deleteWishlistUseCase;
    private final GetWishlistUseCase getWishlistUseCase;
    private final CheckWishlistUseCase checkWishlistUseCase;

    public Long addWishList(Long userId, Long productId) {
        return addWishlistUseCase.addWishlist(userId, productId);
    }

    public void deleteWishList(Long userId, Long productId) {
        deleteWishlistUseCase.deleteWishlist(userId, productId);
    }

    public List<WishlistResponse> getWishList(Long userId) {
        return getWishlistUseCase.getWishlist(userId);
    }

    public boolean checkWishList(Long userId, Long productId) {
        return checkWishlistUseCase.checkWishlist(userId, productId);
    }
}
