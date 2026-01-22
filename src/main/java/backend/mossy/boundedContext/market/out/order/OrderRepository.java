package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("SELECT o FROM Order o JOIN FETCH o.buyer WHERE o.id = :orderId")
    Optional<Order> findByIdWithBuyer(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o JOIN FETCH o.buyer WHERE o.buyer.id = :userId")
    List<Order> findByBuyerIdWithBuyer(@Param("userId") Long userId);
}
