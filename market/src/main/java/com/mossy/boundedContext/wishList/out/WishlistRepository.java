package com.mossy.boundedContext.wishList.out;

import com.mossy.boundedContext.wishList.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<Wishlist, Long>, WishlistRepositoryCustom {

    void deleteByMarketUserIdAndProductItemId(@Param("userId") Long userId, @Param("productItemId") Long productId);

    boolean existsByMarketUserIdAndProductItemId(@Param("userId") Long userId, @Param("productItemId") Long productId);
}
