package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSellerOrderUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, OrderState state, Pageable pageable) {
        return orderRepository.findSellerOrderListBySellerId(sellerId, state, pageable);
    }
}