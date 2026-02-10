package com.mossy.boundedContext.wishList.app;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.out.ProductRepository;
import com.mossy.boundedContext.wishList.domain.Wishlist;
import com.mossy.boundedContext.wishList.out.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddWishlistUseCase {

    private final WishlistRepository wishlistRepository;
    private final MarketUserRepository marketUserRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addWishlist(Long userId, Long productId) {
        MarketUser marketUser = marketUserRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        Wishlist wishList = wishlistRepository.save(Wishlist.create(marketUser, product));

        return wishList.getId();
    }
}
