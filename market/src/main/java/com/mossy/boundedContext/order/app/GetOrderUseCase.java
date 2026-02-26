package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderListResponse> getOrderListByUserId(
            Long userId,
            OrderState state,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        return orderRepository.findOrderListByUserId(userId, state, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        return orderRepository.findOrderDetailsByOrderId(orderId);
    }
}