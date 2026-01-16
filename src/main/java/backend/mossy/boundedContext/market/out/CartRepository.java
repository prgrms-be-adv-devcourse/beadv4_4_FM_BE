package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyerId(Long buyerId);
}
