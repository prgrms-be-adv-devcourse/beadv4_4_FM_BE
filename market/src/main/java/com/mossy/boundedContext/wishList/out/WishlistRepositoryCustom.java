package com.mossy.boundedContext.wishList.out;

import com.mossy.boundedContext.wishList.in.dto.response.WishlistResponse;

import java.util.List;

public interface WishlistRepositoryCustom {
    List<WishlistResponse> findWishlistByUserId(Long userId);
}
