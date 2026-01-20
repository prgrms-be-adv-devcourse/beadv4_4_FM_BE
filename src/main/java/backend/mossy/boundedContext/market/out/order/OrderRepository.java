package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
