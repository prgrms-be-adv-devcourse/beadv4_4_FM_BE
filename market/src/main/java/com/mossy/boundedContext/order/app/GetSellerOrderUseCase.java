package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.in.dto.response.OrderDetailSellerResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSellerOrderUseCase {

    private final OrderRepository orderRepository;

    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, Pageable pageable) {
        return orderRepository.findSellerOrderListBySellerId(sellerId, pageable);
    }

    public OrderDetailSellerResponse getSellerOrderDetail(Long orderDetailId) {
        return orderRepository.findSellerOrderDetailById(orderDetailId);
    }
}