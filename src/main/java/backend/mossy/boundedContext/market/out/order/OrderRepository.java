package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndState(Long orderId, OrderState state);

    Optional<Order> findByOrderNo(String orderNo);
    @Query("SELECT o FROM Order o JOIN FETCH o.buyer WHERE o.id = :orderId")
    Optional<Order> findByIdWithBuyer(@Param("orderId") Long orderId);
}
