package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndState(Long orderId, OrderState state);

    Optional<Order> findByOrderNo(String orderNo);
}
