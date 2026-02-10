package com.mossy.boundedContext.wishList.out;

import com.mossy.boundedContext.wishList.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<Wishlist, Long>, WishlistRepositoryCustom {

    void deleteByMarketUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    boolean existsByMarketUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
