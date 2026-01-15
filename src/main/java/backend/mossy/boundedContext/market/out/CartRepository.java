package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
