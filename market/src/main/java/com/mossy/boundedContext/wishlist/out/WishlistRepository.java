package com.mossy.boundedContext.wishlist.out;

import com.mossy.boundedContext.wishlist.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByMarketUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByMarketUserIdAndProductId(Long userId, Long productId);

    boolean existsByMarketUserIdAndProductId(Long userId, Long productId);
}
