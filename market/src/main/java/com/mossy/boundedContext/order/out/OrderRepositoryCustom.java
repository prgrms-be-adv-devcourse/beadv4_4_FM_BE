package com.mossy.boundedContext.order.out;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.shared.market.enums.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {

    Page<OrderListResponse> findOrderListByUserId(Long userId, OrderState state, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<OrderDetailResponse> findOrderDetailsByOrderId(Long orderId);

    Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, OrderState state, Pageable pageable);

    Page<Order> findPaidOrdersUpdatedBefore(LocalDateTime threshold, Pageable pageable);
}