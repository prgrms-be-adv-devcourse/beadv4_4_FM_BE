package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public Page<OrderListResponse> getOrderListByUserId(
            Long userId,
            Pageable pageable
    ) {
        return orderRepository.findOrderListByUserId(userId, pageable);
    }

    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        return orderRepository.findOrderDetailsByOrderId(orderId);
    }
}