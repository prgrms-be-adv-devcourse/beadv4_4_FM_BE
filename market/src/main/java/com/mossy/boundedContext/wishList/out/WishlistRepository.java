package com.mossy.boundedContext.wishList.out;

import com.mossy.boundedContext.wishList.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByMarketUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByMarketUserIdAndProductId(Long userId, Long productId);

    boolean existsByMarketUserIdAndProductId(Long userId, Long productId);
}
