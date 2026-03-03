package com.mossy.boundedContext.order.out;


import com.mossy.boundedContext.order.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends JpaRepository<Order, Long>, OrderRepositoryCustom{

    @EntityGraph(attributePaths = {"orderItems", "buyer"})
    Optional<Order> findWithItemsById(Long orderId);

    @EntityGraph(attributePaths = {"orderItems", "buyer", "orderItems.userCoupon"})
    Optional<Order> findWithItemsAndCouponsById(Long orderId);

    @EntityGraph(attributePaths = {"orderItems", "buyer", "orderItems.userCoupon"})
    Optional<Order> findByOrderNo(String orderNo);

    @Modifying
    @Query("delete from Order o where o.state = 'EXPIRED' and o.createdAt < :threshold")
    void deleteExpiredOrders(@Param("threshold") LocalDateTime threshold);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.userCoupon"})
    @Query("select o from Order o where o.state = 'PENDING' and o.createdAt < :threshold")
    List<Order> findPendingOrdersCreatedBefore(@Param("threshold") LocalDateTime threshold);
}
