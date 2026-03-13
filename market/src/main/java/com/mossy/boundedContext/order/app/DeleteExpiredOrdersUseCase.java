package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteExpiredOrdersUseCase {

    private final OrderRepository orderRepository;

    @Transactional
    public void execute() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
        orderRepository.deleteExpiredOrders(threshold);
    }
}
