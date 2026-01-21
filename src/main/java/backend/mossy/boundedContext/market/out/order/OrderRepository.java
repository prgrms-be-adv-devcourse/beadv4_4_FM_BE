package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderDetails")
    Optional<Order> findByOrderNo(String orderNo);
}
