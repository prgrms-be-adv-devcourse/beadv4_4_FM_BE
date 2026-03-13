package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetExpiredOrdersUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<Order> execute() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        return orderRepository.findPendingOrdersCreatedBefore(threshold);
    }
}
