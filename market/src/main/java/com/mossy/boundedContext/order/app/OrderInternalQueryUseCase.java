package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.in.dto.response.MarketOrderResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderInternalQueryUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public MarketOrderResponse getOrderById(Long orderId) {
        MarketOrderResponse result = orderRepository.findMarketOrderById(orderId);
        if (result == null) {
            throw new DomainException(ErrorCode.ORDER_NOT_FOUND);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public MarketOrderResponse getOrderByOrderNo(String orderNo) {
        MarketOrderResponse result = orderRepository.findMarketOrderByOrderNo(orderNo);
        if (result == null) {
            throw new DomainException(ErrorCode.ORDER_NOT_FOUND);
        }
        return result;
    }
}

