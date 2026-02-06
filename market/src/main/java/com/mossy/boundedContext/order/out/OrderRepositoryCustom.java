package com.mossy.boundedContext.order.out;

import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailSellerResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.shared.market.payload.OrderPayoutDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {

    List<OrderPayoutDto> findPayoutOrderByOrderId(Long orderId);

    Page<OrderListResponse> findOrderListByUserId(Long userId, Pageable pageable);

    List<OrderDetailResponse> findOrderDetailsByOrderId(Long orderId);

    Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, Pageable pageable);

    OrderDetailSellerResponse findSellerOrderDetailById(Long orderDetailId);
}