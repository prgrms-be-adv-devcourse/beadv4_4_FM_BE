package backend.mossy.boundedContext.market.out.cart;

import backend.mossy.boundedContext.market.domain.cart.Cart;
import backend.mossy.boundedContext.market.out.product.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {"buyer", "items"})
    Optional<Cart> findByBuyerId(Long buyerId);
}
